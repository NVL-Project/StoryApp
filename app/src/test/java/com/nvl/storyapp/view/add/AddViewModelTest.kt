package com.nvl.storyapp.view.add

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nvl.storyapp.data.server.model.Repository
import com.nvl.storyapp.data.server.response.AddStoryResponse
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
class AddViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var newRepository: Repository
    private lateinit var newViewModel: AddViewModel
    private var dummyResponse = MainDummy.generateDummyAddStoryResponse()
    private val dummyMultipart = MainDummy.generateDummyMultipartFile()
    private val dummyDescription = MainDummy.generateDummyDesc()
    private val dummyLat = MainDummy.generateDummylat()
    private val dummyLng = MainDummy.generateDummylng()
    private var bearer = "ascasdcsdcvsd"

    @Before
    fun setUp(){
        newViewModel = AddViewModel(newRepository)
    }


    @Test
    fun `when Add Story Should Not Null and Return Success`() = runTest {
        val expected = MutableLiveData<AddStoryResponse>()
        expected.value = dummyResponse
        Mockito. `when`(newRepository.uploadFile(bearer,dummyMultipart,dummyDescription,dummyLat,dummyLng)).thenReturn(expected)

        val actualNews = newViewModel.uploadStory(bearer,dummyMultipart,dummyDescription,dummyLat,dummyLng).getOrAwaitValue()
        assertNotNull(actualNews)
        assertEquals(dummyResponse, actualNews)
    }
}