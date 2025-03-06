package com.taqiyuddin.storyappdicoding.submissionakhir.view.story

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.taqiyuddin.storyappdicoding.submissionakhir.R
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.StoryAppPreferences
import com.taqiyuddin.storyappdicoding.submissionakhir.data.preferences.dataStore
import com.taqiyuddin.storyappdicoding.submissionakhir.databinding.ActivityStoryBinding
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.LocaleUtils
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.displayToast
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.wrapEspressoIdlingResource
import com.taqiyuddin.storyappdicoding.submissionakhir.view.adapter.LoadingStateAdapter
import com.taqiyuddin.storyappdicoding.submissionakhir.view.adapter.StoryAdapter
import com.taqiyuddin.storyappdicoding.submissionakhir.view.main.MainActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.view.maps.MapsActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private lateinit var recycleStory: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var listStoryAdapter: StoryAdapter
    private lateinit var storyListViewModel: StoryViewModel
    private lateinit var layoutManager: GridLayoutManager
    private val preferences: StoryAppPreferences by lazy {
        StoryAppPreferences.getInstance(this.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViews()
        checkLoginStatus()
        getData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupViews() {
        storyListViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[StoryViewModel::class.java]

        recycleStory = binding.rvStory
        swipeRefreshLayout = binding.swipeRefresh
        layoutManager = GridLayoutManager(this, 1)
        recycleStory.layoutManager = layoutManager
        recycleStory.setHasFixedSize(true)

        listStoryAdapter = StoryAdapter()
        recycleStory.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStoryAdapter.retry()
            }
        )

        swipeRefreshLayout.setOnRefreshListener {
            listStoryAdapter.refresh()
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AppendStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            val isLoggedIn = preferences.isLoggedIn().first()
            if (!isLoggedIn) {
                val intent = Intent(this@StoryActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getData() {
        storyListViewModel.stories.observe(this) { pagingData ->
            listStoryAdapter.submitData(lifecycle, pagingData)
        }

        listStoryAdapter.loadStateFlow.asLiveData().observe(this) { loadState ->
            swipeRefreshLayout.isRefreshing = loadState.refresh is LoadState.Loading
            binding.progressBar.visibility = if (loadState.refresh is LoadState.Loading) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }

            if (loadState.refresh is LoadState.NotLoading) {
                refreshing()
                if (listStoryAdapter.itemCount == 0) {
                    binding.emptyView.visibility = android.view.View.VISIBLE
                } else {
                    binding.emptyView.visibility = android.view.View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_language -> {
                showLanguageDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Indonesia")
        var selectedLanguage: Int

        lifecycleScope.launch {
            val currentLanguage = preferences.getLanguage().first()
            selectedLanguage = if (currentLanguage == "in") 1 else 0

            AlertDialog.Builder(this@StoryActivity)
                .setTitle(getString(R.string.change_language))
                .setSingleChoiceItems(languages, selectedLanguage) { dialog, which ->
                    val newLanguage = if (which == 1) "in" else "en"
                    lifecycleScope.launch {
                        preferences.setLanguage(newLanguage)
                        applyLanguageAndRestart(newLanguage)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun applyLanguageAndRestart(languageCode: String) {
        val context = LocaleUtils.setLocale(this, languageCode)
        resources.updateConfiguration(
            context.resources.configuration,
            context.resources.displayMetrics
        )

        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        intent?.let { startActivity(it) }
        finish()
    }

    private fun logout() {
        val title = getString(R.string.logout)
        val message = getString(R.string.logout_message)
        val builder = AlertDialog.Builder(this@StoryActivity)

        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                loggingOut()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }


    private fun loggingOut() {
        displayToast(this, getString(R.string.loggingout))
        lifecycleScope.launch {
            wrapEspressoIdlingResource {
                preferences.clearUser()
            }

            val intent = Intent(this@StoryActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun refreshing() {
        recycleStory.post {
            recycleStory.smoothScrollToPosition(0)
        }
    }
}