package com.taqiyuddin.storyappdicoding.submissionakhir.view.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.DummyDataTest
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.MainDispatcherRule
import com.taqiyuddin.storyappdicoding.submissionakhir.data.repositories.StoryRepository
import com.taqiyuddin.storyappdicoding.submissionakhir.data.response.DetailedStory
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.getOrAwaitValue
import com.taqiyuddin.storyappdicoding.submissionakhir.view.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DummyDataTest.generateDummyStoryResponse()
        val data: PagingData<DetailedStory> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<DetailedStory>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getPagingStories()).thenReturn(expectedStory)

        val storyListViewModel = StoryViewModel(storyRepository)
        val actualStories: PagingData<DetailedStory> = storyListViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<DetailedStory> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<DetailedStory>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getPagingStories()).thenReturn(expectedStory)

        val storyListViewModel = StoryViewModel(storyRepository)
        val actualStory: PagingData<DetailedStory> = storyListViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        Assert.assertEquals(0, differ.snapshot().size)
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<DetailedStory>>>() {
    companion object {
        fun snapshot(items: List<DetailedStory>): PagingData<DetailedStory> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<DetailedStory>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<DetailedStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}