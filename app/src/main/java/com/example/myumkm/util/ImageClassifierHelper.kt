package com.example.myumkm.util

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.example.myumkm.ml.MoneyModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class ImageClassifierHelper(
    var threshold: Float = 0.1f,
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

    fun setupModel() {
        try {
            val options = Interpreter.Options()
            options.numThreads = numThreads
            val assetManager = context.assets
            val model = assetManager.openFd(modelName)
            val inputStream = model.createInputStream()
            val fileChannel = FileInputStream(inputStream.fd).channel
            val startOffset = model.startOffset
            val declaredLength = model.declaredLength
            val fileDescriptor = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            interpreter = Interpreter(fileDescriptor, options)
        } catch (e: Exception) {
            imageClassifierListener?.onError(
                "Model failed to initialize. See error logs for details"
            )
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

