package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.view.adapter.ArticleAdapter
import com.dicoding.asclepius.viewmodel.ResultViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ResultActivity : AppCompatActivity() {
    private val resultViewModel by viewModel<ResultViewModel>()

    private lateinit var binding: ActivityResultBinding

    private val articleAdapter = ArticleAdapter()
    private val displayedResult: String? by lazy { intent.getStringExtra(EXTRA_RESULT) }
    private val inferenceTime: String? by lazy { intent.getStringExtra(EXTRA_INFERENCE_TIME) }
    private val imageUri: Uri by lazy { Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        setViews()
        setListeners()
        observeArticles()
    }

    private fun setViews() {
        binding.apply {
            resultImage.setImageURI(imageUri)
            tvInferenceTime.text = StringBuilder("$inferenceTime ms")
            resultText.text = displayedResult

            rvArticles.apply {
                articleAdapter.onItemClick = { url ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                adapter = articleAdapter
                layoutManager = LinearLayoutManager(this@ResultActivity)
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnRetry.setOnClickListener {
                resultViewModel.getArticles()
            }
        }
    }

    private fun observeArticles() {
        resultViewModel.apply {
            isLoading.observe(this@ResultActivity) {
                shimmerToggle(it)
            }

            listArticle.observe(this@ResultActivity) {
                binding.articleInfoLayout.isVisible = it.isNullOrEmpty()
                binding.rvArticles.isVisible = !it.isNullOrEmpty()

                if (it.isNullOrEmpty()) {
                    binding.tvArticleInfo.text =
                        ContextCompat.getString(this@ResultActivity, R.string.empty_articles)
                } else {
                    articleAdapter.submitList(it)
                }
            }

            textMessage.observe(this@ResultActivity) {
                binding.apply {
                    articleInfoLayout.isVisible = it.isNotEmpty()
                    tvArticleInfo.text = it.toString()
                }
            }
        }
    }

    private fun shimmerToggle(isLoading: Boolean) {
        binding.apply {
            shimmerLayout.isVisible = isLoading
            articleInfoLayout.isVisible = false
            rvArticles.isVisible = false

            if (isLoading) {
                shimmerLayout.startShimmer()
            } else {
                shimmerLayout.stopShimmer()
            }
        }
    }

    companion object {
        const val EXTRA_RESULT = "extra_results"
        const val EXTRA_INFERENCE_TIME = "extra_inference_time"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}