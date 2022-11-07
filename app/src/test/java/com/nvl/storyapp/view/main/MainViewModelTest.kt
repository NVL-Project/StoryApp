package com.nvl.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.nvl.storyapp.data.server.model.Repository
import com.nvl.storyapp.data.server.response.StoryItem
import com.nvl.storyapp.utils.MainDispatcherRule
import com.nvl.storyapp.utils.MainDummy
import com.nvl.storyapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var newRepository: Repository
    private lateinit var newViewModel: MainViewModel
    private var dummyNews = MainDummy.generateDummyStory()
    private var bearer = "ascasdcsdcvsd"


    @Before
    fun setUp(){
        newViewModel = MainViewModel(newRepository)
    }


    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val data : PagingData<StoryItem> = PagingData.from(dummyNews)
        val expected = MutableLiveData<PagingData<StoryItem>>()
        expected.value = data
        Mockito. `when`(newRepository.getStories(bearer)).thenReturn(expected)

        val actual: PagingData<StoryItem> = newViewModel.getStories(bearer).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        assertNotNull(differ.snapshot())
        assertEquals(dummyNews, differ.snapshot())
        assertEquals(dummyNews.size, differ.snapshot().size)

    }

}
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}