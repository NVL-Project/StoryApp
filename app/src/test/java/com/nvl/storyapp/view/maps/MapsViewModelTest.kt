package com.nvl.storyapp.view.maps

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nvl.storyapp.data.server.model.Repository
import com.nvl.storyapp.data.server.response.ListStoryResponse
import com.nvl.storyapp.utils.MainDispatcherRule
import com.nvl.storyapp.utils.MainDummy
import com.nvl.storyapp.utils.getOrAwaitValue
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
class MapsViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var newRepository: Repository
    private lateinit var newViewModel: MapsViewModel
    private var dummyNews = MainDummy.generateDummyStoryResponse()
    private var bearer = "ascasdcsdcvsd"

    @Before
    fun setUp(){
        newViewModel = MapsViewModel(newRepository)
    }


    @Test
    fun `when Get Story width location Should Not Null and Return Success`() = runTest {
        val expected = MutableLiveData<ListStoryResponse>()
        expected.value = dummyNews
        Mockito. `when`(newRepository.getStoryMaps(bearer)).thenReturn(expected)

        val actualNews = newViewModel.getMaps(bearer).getOrAwaitValue()
        assertNotNull(actualNews)
        assertEquals(dummyNews, actualNews)
    }

}