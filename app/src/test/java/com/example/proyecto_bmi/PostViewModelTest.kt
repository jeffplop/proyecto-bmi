package com.example.proyecto_bmi

import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.local.repository.PostRepository
import com.example.proyecto_bmi.viewmodel.PostViewModel
import io.kotest.core.spec.style.StringSpec
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

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeTest { Dispatchers.setMain(dispatcher) }
    afterTest { Dispatchers.resetMain() }

    "fetchPosts debe actualizar postList con datos del repositorio" {
        runTest(dispatcher) {
            val mockPostRepo = mockk<PostRepository>()
            val mockUserRepo = mockk<UsuarioRepository>()

            val fakePosts = listOf(Post(1, 1, "Test", "Body"))

            coEvery { mockPostRepo.getPosts() } returns fakePosts
            coEvery { mockUserRepo.obtenerTodosLosUsuariosLocalmente() } returns emptyList()

            val viewModel = PostViewModel(mockUserRepo, mockPostRepo)

            advanceUntilIdle()

            viewModel.postList.value.size shouldBe 1
            viewModel.postList.value[0].title shouldBe "Test"
        }
    }
})