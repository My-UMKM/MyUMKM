package com.example.myumkm.ui.legal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.myumkm.databinding.ActivityLegalBinding
import com.example.myumkm.ui.camera.CameraActivity
import com.example.myumkm.ui.camera.CameraActivity.Companion.CAMERAX_RESULT
import com.example.myumkm.util.ImageClassifierHelper
import com.example.myumkm.util.arrayOfMoneyIndex
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class LegalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLegalBinding

    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            imageClassifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@LegalActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }
//                override fun onResults(results: Array<FloatArray>?, inferenceTime: Long) {
//                    runOnUiThread {
//                        results?.let { tensorBuffer ->
//                            // Handle the results here
////                            val array = tensorBuffer.floatArray.withIndex().maxBy { it.value }
////                            val maxIndex = array.index
////                            val maxValue = array.value
////                            Toast.makeText(this@LegalActivity, "Results: ${arrayOfMoneyIndex.classNames[maxIndex]} and have value ${maxValue}, Inference Time: $inferenceTime", Toast.LENGTH_SHORT).show()
////                            // For example, you can print the results:
//////                            Toast.makeText(this@LegalActivity, "Results: ${tensorBuffer.floatArray[0]}, Inference Time: $inferenceTime", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
                override fun onResults(results: Array<FloatArray>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { tensorBuffer ->
                            // Find the index with the highest confidence score
                            val maxIndex = tensorBuffer[0].indices.maxBy { tensorBuffer[0][it] } ?: -1
                            val maxValue = tensorBuffer[0][maxIndex]

                            // Display the results in a Toast message
                            Toast.makeText(this@LegalActivity, "Results: Class ${arrayOfMoneyIndex.classNames[maxIndex]} with confidence ${maxValue}, Inference Time: $inferenceTime", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        )

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d("Image URI", "showImage: $uri")
            binding.previewImageView.setImageURI(uri)

            // Load the image from the URI
            val bitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }

            // Classify the image
            imageClassifierHelper.classify(bitmap)
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}