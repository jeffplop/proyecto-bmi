package com.example.proyecto_bmi.viewmodel

import com.example.proyecto_bmi.data.local.SessionManager
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.model.Favorite
import com.example.proyecto_bmi.data.remote.model.Post
import com.example.proyecto_bmi.data.local.repository.PostRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var mockPostRepo: PostRepository
    private lateinit var mockUserRepo: UsuarioRepository
    private lateinit var mockSessionManager: SessionManager
    private lateinit var viewModel: PostViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns dispatcher

        mockPostRepo = mockk(relaxed = true)
        mockUserRepo = mockk(relaxed = true)
        mockSessionManager = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun fetchPosts_debe_actualizar_postList_correctamente() = runTest(dispatcher) {
        val fakePosts = listOf(
            Post(1, 101, "Manual Test 1", "Body 1", null, "1.0", "2024", "Excell", false, 1),
            Post(1, 102, "Manual Test 2", "Body 2", null, "2.0", "2024", "Zebra", true, 2)
        )
        coEvery { mockPostRepo.getPosts() } returns fakePosts
        coEvery { mockSessionManager.getUserId() } returns -1

        viewModel = PostViewModel(mockUserRepo, mockSessionManager, mockPostRepo)
        advanceUntilIdle()

        assertEquals(2, viewModel.postList.value.size)
        assertEquals("Manual Test 1", viewModel.postList.value[0].title)
    }

    @Test
    fun fetchCurrentUserRole_debe_identificar_ADMIN() = runTest(dispatcher) {
        val adminUser = UsuarioEntity(1, "Admin", "admin@bmi.cl", "123", "+569", "Estandar")
        coEvery { mockSessionManager.getUserId() } returns 1
        coEvery { mockUserRepo.obtenerUsuarioPorId(1) } returns adminUser

        viewModel = PostViewModel(mockUserRepo, mockSessionManager, mockPostRepo)
        advanceUntilIdle()

        assertEquals("ADMIN", viewModel.userRole.value)
    }

    @Test
    fun deletePost_debe_llamar_al_repositorio_y_actualizar_mensaje() = runTest(dispatcher) {
        coEvery { mockPostRepo.deletePost(101) } returns true
        coEvery { mockPostRepo.getPosts() } returns emptyList()

        viewModel = PostViewModel(mockUserRepo, mockSessionManager, mockPostRepo)
        viewModel.deletePost(101)
        advanceUntilIdle()

        coVerify { mockPostRepo.deletePost(101) }
        assertEquals("Manual eliminado", viewModel.operationMessage.value)
    }

    @Test
    fun saveOrUpdatePost_crear_debe_llamar_createPost() = runTest(dispatcher) {
        val newPost = Post(0, 0, "Nuevo", "Desc", null, "1.0", "2025", "Test", false, 1)
        coEvery { mockPostRepo.createPost(any()) } returns true
        coEvery { mockSessionManager.getUserId() } returns 1

        viewModel = PostViewModel(mockUserRepo, mockSessionManager, mockPostRepo)
        viewModel.saveOrUpdatePost(newPost, isEdit = false)
        advanceUntilIdle()

        coVerify { mockPostRepo.createPost(any()) }
        assertEquals("Creado correctamente", viewModel.operationMessage.value)
        assertTrue(viewModel.saveSuccess.value)
    }

    @Test
    fun saveOrUpdatePost_editar_debe_llamar_updatePost() = runTest(dispatcher) {
        val existingPost = Post(userId = 1, id = 55, title = "Editado", body = "Desc", fabricante = "Test")
        coEvery { mockPostRepo.updatePost(55, any()) } returns true

        viewModel = PostViewModel(mockUserRepo, mockSessionManager, mockPostRepo)
        viewModel.saveOrUpdatePost(existingPost, isEdit = true)
        advanceUntilIdle()

        coVerify { mockPostRepo.updatePost(55, any()) }
        assertEquals("Actualizado correctamente", viewModel.operationMessage.value)
    }

    @Test
    fun toggleFavorite_remover_debe_actualizar_lista_y_mensaje() = runTest(dispatcher) {
        val post = Post(1, 101, "Test", "Body", null, "1.0", "2024", "Test", false, 1)
        val favoritesList = listOf(Favorite(1, 1, 101))

        coEvery { mockSessionManager.getUserId() } returns 1
        coEvery { mockPostRepo.getFavorites(1) } returns favoritesList
        coEvery { mockPostRepo.toggleFavorite(1, 101, true) } returns true

        viewModel = PostViewModel(mockUserRepo, mockSessionManager, mockPostRepo)
        advanceUntilIdle()

        viewModel.toggleFavorite(post)
        advanceUntilIdle()

        coVerify { mockPostRepo.toggleFavorite(1, 101, true) }
        assertEquals("Eliminado de Favoritos", viewModel.operationMessage.value)
    }
}