package com.example.proyecto_bmi.viewmodel

import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.repository.PostRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun fetchPosts_debe_actualizar_postList_con_datos() = runTest(dispatcher) {

        val mockPostRepo = mockk<PostRepository>()
        val mockUserRepo = mockk<UsuarioRepository>(relaxed = true)
        val mockSession = mockk<SessionManager>(relaxed = true)

        val fakePosts = listOf(
            Post(1, 101, "Manual A", "Body A", null, "1.0", "2024", "Excell", false, 1),
            Post(1, 102, "Manual B", "Body B", null, "1.0", "2024", "Zebra", true, 2)
        )

        coEvery { mockPostRepo.getPosts() } returns fakePosts

        val viewModel = PostViewModel(mockUserRepo, mockSession, mockPostRepo)

        advanceUntilIdle()

        viewModel.postList.value.size shouldBe 2
        viewModel.postList.value[0].title shouldBe "Manual A"
    }

    @Test
    fun getPostById_debe_actualizar_selectedPost() = runTest(dispatcher) {
        val mockPostRepo = mockk<PostRepository>()
        val mockUserRepo = mockk<UsuarioRepository>(relaxed = true)
        val mockSession = mockk<SessionManager>(relaxed = true)

        val targetPost = Post(1, 99, "Target Manual", "Desc", null, "1.0", "2024", "Test", false, 1)

        coEvery { mockPostRepo.getPostById(99) } returns targetPost
        coEvery { mockPostRepo.getPosts() } returns emptyList() // Para el init

        val viewModel = PostViewModel(mockUserRepo, mockSession, mockPostRepo)
        viewModel.getPostById(99)
        advanceUntilIdle()

        viewModel.selectedPost.value shouldBe targetPost
        viewModel.errorMessage.value shouldBe null
    }

    @Test
    fun getPostById_debe_manejar_error() = runTest(dispatcher) {
        val mockPostRepo = mockk<PostRepository>()
        val mockUserRepo = mockk<UsuarioRepository>(relaxed = true)
        val mockSession = mockk<SessionManager>(relaxed = true)

        coEvery { mockPostRepo.getPostById(999) } returns null
        coEvery { mockPostRepo.getPosts() } returns emptyList()

        val viewModel = PostViewModel(mockUserRepo, mockSession, mockPostRepo)
        viewModel.getPostById(999)
        advanceUntilIdle()

        viewModel.selectedPost.value shouldBe null
        viewModel.errorMessage.value shouldBe "Error al cargar manual."
    }
}