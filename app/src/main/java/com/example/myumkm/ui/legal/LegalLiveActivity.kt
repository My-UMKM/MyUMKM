package com.example.myumkm.ui.legal

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.myumkm.databinding.ActivityLegalLiveBinding
import com.example.myumkm.util.ImageClassifierHelper
import com.example.myumkm.util.arrayOfProduct
import com.example.myumkm.util.toast
import java.util.concurrent.Executors

@ExperimentalGetImage class LegalLiveActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLegalLiveBinding
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
        binding = ActivityLegalLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        startCamera()
    }

    private fun startCamera() {
        imageClassifierHelper =
            ImageClassifierHelper(
                sizeObject = arrayOfProduct.classNames.size,
                context = this,
                modelName = "Product-Image-Classification",
                imageClassifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        runOnUiThread {
                            toast(error)
                        }
                    }

                    override fun onResults(results: Array<FloatArray>?, inferenceTime: Long) {
                        runOnUiThread {
                            results?.let { tensorBuffer ->
                                // Find the index with the highest confidence score
                                val maxIndex = tensorBuffer[0].indices.maxBy { tensorBuffer[0][it] }
                                val maxValue = tensorBuffer[0][maxIndex]



                                binding.tvResult.text = "Class ${arrayOfProduct.classNames[maxIndex]} with confidence $maxValue"
                            }
                        }
                    }

                }
            )

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer =
                ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(binding.viewFinder.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    // The analyzer can then be assigned to the instance
                    .also {
                        it.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                            val bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )

                            image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
                            image.close()
                            bitmapBuffer.let { it1 -> imageClassifierHelper.classify(it1) }
                        }
                    }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Toast.makeText(this, "Gagal memunculkan kamera.", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}