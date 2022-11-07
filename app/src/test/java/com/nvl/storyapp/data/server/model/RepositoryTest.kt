package com.nvl.storyapp.data.server.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.nvl.storyapp.api.ApiService
import com.nvl.storyapp.data.local.UserPreference
import com.nvl.storyapp.data.local.database.StoryDatabase
import com.nvl.storyapp.data.server.response.*
import com.nvl.storyapp.utils.MainDispatcherRule
import com.nvl.storyapp.utils.MainDummy
import com.nvl.storyapp.utils.getOrAwaitValue
import com.nvl.storyapp.view.main.MainAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

//@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: Repository

    @Mock
    private lateinit var serverDataSource: ServerDataSource
    private lateinit var userPreference: UserPreference
    private lateinit var storyDatabase: StoryDatabase
    private lateinit var apiService: ApiService
    private var dummyLogin = MainDummy.generateDummyLogin()
    private var dummyRegister = MainDummy.generateDummyRegister()
    private var dummyStoryResponse = MainDummy.generateDummyStoryResponse()
    private var dummyAddResponse = MainDummy.generateDummyAddStoryResponse()
    private val dummyMultipart = MainDummy.generateDummyMultipartFile()
    private val dummyDescription = MainDummy.generateDummyDesc()
    private var dummyStory = MainDummy.generateDummyStory()
    private val dummyLat = MainDummy.generateDummylat()
    private val dummyLng = MainDummy.generateDummylng()
    private val dummyLoginResult = MainDummy.generateDummyLoginResult()

    private var name = "cobak"
    private val email = "coba@gmail.com"
    private val password = "1213453"
    private var bearer = "ascasdcsdcvsd"

    @Before
    fun setUp() {
        serverDataSource = mock(ServerDataSource::class.java)
        userPreference = mock(UserPreference::class.java)
        storyDatabase = mock(StoryDatabase::class.java)
        apiService = mock(ApiService::class.java)
        repository = Repository(serverDataSource,userPreference,storyDatabase,apiService)
    }

    @Test
    fun `when Post Login Should Not Null and Return Success`() = runTest {
        val expected = MutableLiveData<LoginResponse>()
        expected.value = dummyLogin
        Mockito.`when`(repository.login(email, password)).thenReturn(expected)

        val actualNews = repository.login(email, password).getOrAwaitValue()
        Assert.assertNotNull(actualNews)
        Assert.assertEquals(dummyLogin, actualNews)
    }

    @Test
    fun `when Post Register Should Not Null and Return Success`() = runTest {
        val expected = MutableLiveData<RegisterResponse>()
        expected.value = dummyRegister
        Mockito.`when`(repository.register(name, email, password)).thenReturn(expected)

        val actualNews = repository.register(name, email, password).getOrAwaitValue()
        Assert.assertNotNull(actualNews)
        Assert.assertEquals(dummyRegister, actualNews)
    }


    @Test
    fun `when save Should Exist in User`() = runTest {
        val sample = dummyLoginResult
        val expected = MutableLiveData<LoginResult>()
        expected.value = LoginResult(
            "user-POMx0JfdJ81yyOKP",
            "Cobak",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVBPTXgwSmZkSjgxeXlPS1AiLCJpYXQiOjE2NjcxNDk2NDd9.TrbiwAXZMgR2FpbTLOrDh0XAYp0uFgGCHt-S_dQ73iU",)
        Mockito. `when`(repository.getUser()).thenReturn(expected)
        val actual = repository.getUser().getOrAwaitValue()
        Assert.assertEquals("Cobak", actual.userId)
        Assert.assertTrue(actual == sample)
    }

    @Test
    fun `when delete Should Not Exist in User`() = runTest {
        val sample = dummyLoginResult
        val expected = MutableLiveData<LoginResult>()
        expected.value = LoginResult(
            "user-POMx0JfdJ81yyOKP",
            "Cobak",
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVBPTXgwSmZkSjgxeXlPS1AiLCJpYXQiOjE2NjcxNDk2NDd9.TrbiwAXZMgR2FpbTLOrDh0XAYp0uFgGCHt-S_dQ73iU",)
        Mockito. `when`(repository.getUser()).thenReturn(expected)
        val actual = repository.deleteUser()
        Assert.assertFalse(actual.equals(sample))

    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val data : PagingData<StoryItem> = PagingData.from(dummyStory)
        val expected = MutableLiveData<PagingData<StoryItem>>()
        expected.value = data
        Mockito. `when`(repository.getStories(bearer)).thenReturn(expected)

        val actual: PagingData<StoryItem> = repository.getStories(bearer).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory, differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)

    }

    @Test
    fun `when Add Story Should Not Null and Return Success`() = runTest {
        val expected = MutableLiveData<AddStoryResponse>()
        expected.value = dummyAddResponse
        Mockito. `when`(repository.uploadFile(bearer,dummyMultipart,dummyDescription,dummyLat,dummyLng)).thenReturn(expected)

        val actualNews = repository.uploadFile(bearer,dummyMultipart,dummyDescription,dummyLat,dummyLng).getOrAwaitValue()
        Assert.assertNotNull(actualNews)
        Assert.assertEquals(dummyAddResponse, actualNews)
    }

    @Test
    fun `when Get Story width location Should Not Null and Return Success`() = runTest {
        val expected = MutableLiveData<ListStoryResponse>()
        expected.value = dummyStoryResponse
        Mockito.`when`(repository.getStoryMaps(bearer)).thenReturn(expected)

        val actualNews = repository.getStoryMaps(bearer).getOrAwaitValue()
        Assert.assertNotNull(actualNews)
        Assert.assertEquals(dummyStoryResponse, actualNews)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}