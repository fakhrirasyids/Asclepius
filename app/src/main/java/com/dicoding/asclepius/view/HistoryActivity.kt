package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.models.History
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.utils.Event
import com.dicoding.asclepius.view.adapter.HistoryAdapter
import com.dicoding.asclepius.viewmodel.HistoryViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val historyViewModel by viewModel<HistoryViewModel>()

    private val historyAdapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()
        initHistoryAction()

        observeViewModel()
        setListeners()
    }

    private fun setRecyclerView() {
        binding.rvHistory.apply {
            historyAdapter.onHistoryClick = { history, ivHistory ->
                val iResult = Intent(this@HistoryActivity, ResultActivity::class.java)
                iResult.putExtra(ResultActivity.EXTRA_RESULT, history.results)
                iResult.putExtra(ResultActivity.EXTRA_INFERENCE_TIME, history.inferenceTime)
                iResult.putExtra(ResultActivity.EXTRA_IMAGE_URI, history.imageUri)
                startActivity(
                    iResult, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@HistoryActivity,
                        androidx.core.util.Pair(ivHistory, "imageClassified"),
                    )
                        .toBundle()
                )
            }
            historyAdapter.addLoadStateListener { loadState ->
                showEmptyHistory(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && historyAdapter.itemCount < 1)
            }
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun observeViewModel() {
        historyViewModel.apply {
            listHistories.observe(this@HistoryActivity) {
                historyAdapter.submitData(lifecycle, it)
                showEmptyHistory(historyAdapter.itemCount < 1)
            }

            snackbarText.observe(this@HistoryActivity) {
                showSnackbar(it)
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
        }
    }

    private fun showSnackbar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Undo") {
            historyViewModel.insertHistory(historyViewModel.undo.value?.getContentIfNotHandled() as History)
        }.show()
    }

    private fun showEmptyHistory(flag: Boolean) {
        binding.apply {
            layoutEmptyHistory.isVisible = flag
            rvHistory.isVisible = !flag
        }
    }

    private fun initHistoryAction() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val history = (viewHolder as HistoryAdapter.ViewHolder).getHistory
                historyViewModel.deleteHistory(history)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvHistory)
    }
}