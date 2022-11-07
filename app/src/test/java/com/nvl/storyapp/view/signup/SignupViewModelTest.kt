package com.nvl.storyapp.view.signup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nvl.storyapp.data.server.model.Repository
import com.nvl.storyapp.data.server.response.RegisterResponse
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
class SignupViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var newRepository: Repository
    private lateinit var newViewModel: SignupViewModel
    private var dummyNews = MainDummy.generateDummyRegister()
    private val name = "coba"
    private val email = "coba@gmail.com"
    private val password = "1213453"

    @Before
    fun setUp(){
        newViewModel = SignupViewModel(newRepository)
    }

    @Test
    fun `when Post Register Should Not Null and Return Success`() = runTest{
        val expected = MutableLiveData<RegisterResponse>()
        expected.value = dummyNews
        Mockito. `when`(newRepository.register(name, email,password)).thenReturn(expected)

        val actualNews = newViewModel.register(name, email, password).getOrAwaitValue()
        assertNotNull(actualNews)
        assertEquals(dummyNews, actualNews)
    }
}