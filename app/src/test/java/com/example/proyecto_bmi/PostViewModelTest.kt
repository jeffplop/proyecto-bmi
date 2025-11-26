package com.example.proyecto_bmi.viewmodel

import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.repository.PostRepository
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
import org.junit.jupiter.api.Assertions.assertEquals
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
    fun fetchPosts_debe_actualizar_postList_con_datos_del_repositorio() = runTest(dispatcher) {
        val mockPostRepo = mockk<PostRepository>()
        val mockUserRepo = mockk<UsuarioRepository>(relaxed = true)
        val mockSessionManager = mockk<SessionManager>(relaxed = true)

        val fakePosts = listOf(
            Post(1, 1, "Test Title", "Test Body", null, "1.0", "2024", "Test", false, 1)
        )

        coEvery { mockPostRepo.getPosts() } returns fakePosts
        coEvery { mockUserRepo.obtenerTodosLosUsuariosLocalmente() } returns emptyList()

        val viewModel = PostViewModel(mockUserRepo, mockSessionManager, mockPostRepo)

        advanceUntilIdle()

        assertEquals(1, viewModel.postList.value.size)
        assertEquals("Test Title", viewModel.postList.value[0].title)
    }
}