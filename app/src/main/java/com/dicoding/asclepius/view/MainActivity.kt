package com.dicoding.asclepius.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.models.History
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ResultActivity.Companion.EXTRA_IMAGE_URI
import com.dicoding.asclepius.view.ResultActivity.Companion.EXTRA_INFERENCE_TIME
import com.dicoding.asclepius.view.ResultActivity.Companion.EXTRA_RESULT
import com.dicoding.asclepius.viewmodel.MainViewModel
import com.yalantis.ucrop.UCrop
import org.koin.android.viewmodel.ext.android.viewModel
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModel<MainViewModel>()

    private var imageClassifierHelper: ImageClassifierHelper? = null
    private var currentImageUri: Uri? = null

    private val uCropContract = object : ActivityResultContract<Uri, Uri>() {
        override fun createIntent(context: Context, input: Uri): Intent {
            val destinationUri = File(
                cacheDir,
                StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
            )
            val options = UCrop.Options()

            val uCrop = UCrop.of(input, Uri.fromFile(destinationUri)).withOptions(options)
            return uCrop.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return UCrop.getOutput(intent!!)!!
        }
    }

    private val cropImage = registerForActivityResult(uCropContract) { uri ->
        currentImageUri = uri
        binding.previewImageView.setImageURI(uri)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setListeners()
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            inflateMenu(R.menu.main_menu)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.menu_history) {
                    val iHistory = Intent(this@MainActivity, HistoryActivity::class.java)
                    startActivity(iHistory)
                }
                true
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            galleryButton.setOnClickListener {
                startGallery()
            }

            analyzeButton.setOnClickListener {
                if (currentImageUri == null) {
                    showToast("Add an Image from gallery first!")
                } else {
                    analyzeImage()
                }
            }
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        cropImage.launch(currentImageUri)
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        if (imageClassifierHelper == null) {
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        runOnUiThread {
                            showToast(error)
                        }
                    }

                    override fun onLoading(isLoading: Boolean) {
                        showLoading(isLoading)
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        runOnUiThread {
                            results?.let { categoriesList ->
                                if (categoriesList.isNotEmpty() && categoriesList[0].categories.isNotEmpty()) {
                                    println(categoriesList)
                                    val sortedCategories =
                                        categoriesList[0].categories.sortedByDescending { it?.score }
                                    val displayResult =
                                        sortedCategories.joinToString("\n") {
                                            "${it.label} " + NumberFormat.getPercentInstance()
                                                .format(it.score).trim()
                                        }

                                    saveToDb(displayResult, inferenceTime.toString())
                                    moveToResult(displayResult, inferenceTime.toString())
                                } else {
                                    showToast("There is no result!")
                                }
                            }
                        }
                    }

                }
            )
        }

        imageClassifierHelper?.classifyStaticImage(currentImageUri!!)
    }

    private fun saveToDb(results: String, inferenceTime: String) {
        mainViewModel.addHistory(
            History(
                imageUri = currentImageUri.toString(),
                inferenceTime = inferenceTime,
                results = results
            )
        )
    }

    private fun moveToResult(results: String, inferenceTime: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(EXTRA_RESULT, results)
        intent.putExtra(EXTRA_INFERENCE_TIME, inferenceTime)
        intent.putExtra(EXTRA_IMAGE_URI, currentImageUri.toString())
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressIndicator.isActivated = isLoading
            progressIndicator.isVisible = isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}