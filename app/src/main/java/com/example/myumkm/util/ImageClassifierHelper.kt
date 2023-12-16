package com.example.myumkm.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream

class ImageClassifierHelper(
    var threshold: Float = 0f,
    var maxResults: Int = 3,
    var numThreads: Int = 4,
    var modelName: String,
    var sizeObject: Int,
    val context: Context,
    val imageClassifierListener: ClassifierListener?
) {
    private var interpreter: Interpreter? = null

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: Array<FloatArray>?,
            inferenceTime: Long
        )
    }

    init {
        setupModel()
    }

    private fun setupModel() {
        try {
            val conditions = CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build()

            FirebaseModelDownloader.getInstance().getModel(modelName, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener { customModel: CustomModel ->
                    val options = Interpreter.Options()
                    options.numThreads = numThreads
                    val modelFile = customModel.file
                    if (modelFile != null) {
                        interpreter = Interpreter(modelFile, options)
                    }
                }
                .addOnFailureListener { e ->
                    imageClassifierListener?.onError("Failed to download the model. See error logs for details.")
                    Log.e("ImageClassifierHelper", "Failed to download the model with error: " + e.message)
                }
        } catch (e: Exception) {
            imageClassifierListener?.onError("Model failed to initialize. See error logs for details.")
            Log.e("ImageClassifierHelper", "TFLite failed to load model with error: " + e.message)
        }
    }

    fun classify(bitmap: Bitmap) {
        if (interpreter == null) {
            setupModel()
        }
        var inferenceTime = SystemClock.uptimeMillis()
        val rgbBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val resizeBitmap = Bitmap.createScaledBitmap(rgbBitmap, 128, 128, true)

        // Convert the image to float32 and normalize to [0, 1]
        val intValues = IntArray(128 * 128)
        val floatValues = FloatArray(128 * 128 * 3)
        resizeBitmap.getPixels(intValues, 0, resizeBitmap.width, 0, 0, resizeBitmap.width, resizeBitmap.height)
        for (i in intValues.indices) {
            val value = intValues[i]
            floatValues[i * 3 + 0] = ((value shr 16 and 0xFF) / 255.0f)
            floatValues[i * 3 + 1] = ((value shr 8 and 0xFF) / 255.0f)
            floatValues[i * 3 + 2] = ((value and 0xFF) / 255.0f)
        }

        // Add an extra dimension at the start of the array
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 3), DataType.FLOAT32)
        inputFeature0.loadArray(floatValues)

        // Run inference
        val outputs = Array(1) { FloatArray(sizeObject) } // Adjust this to the number of your classes
        interpreter?.run(inputFeature0.buffer.rewind(), outputs)

        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        imageClassifierListener?.onResults(
            outputs,
            inferenceTime
        )
    }
}


