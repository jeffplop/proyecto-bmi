package com.example.proyecto_bmi.viewmodel

import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.local.repository.CatalogoRepository
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: CatalogoRepository
    private lateinit var viewModel: CatalogoViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns dispatcher
        mockRepository = mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun fetchCategories_debe_cargar_lista_de_categorias() = runTest(dispatcher) {
        val fakeCategories = listOf(
            CategoryRemote(1, "Balanzas", "Ind"),
            CategoryRemote(2, "Indicadores", "Led")
        )
        coEvery { mockRepository.getCategories() } returns fakeCategories

        viewModel = CatalogoViewModel(mockRepository)
        advanceUntilIdle()

        assertEquals(2, viewModel.categories.value.size)
        assertEquals("Balanzas", viewModel.categories.value[0].nombre)
    }

    @Test
    fun saveCategory_crear_debe_llamar_createCategory() = runTest(dispatcher) {
        coEvery { mockRepository.getCategories() } returns emptyList()
        coEvery { mockRepository.createCategory(any()) } returns true

        viewModel = CatalogoViewModel(mockRepository)
        val newCategory = CategoryRemote(0, "Nueva", "Desc")

        viewModel.saveCategory(newCategory)
        advanceUntilIdle()

        coVerify { mockRepository.createCategory(any()) }
        assertEquals("Categoría creada", viewModel.operationMessage.value)
    }

    @Test
    fun deleteCategory_debe_llamar_deleteCategory_y_actualizar_mensaje() = runTest(dispatcher) {
        coEvery { mockRepository.getCategories() } returns emptyList()
        coEvery { mockRepository.deleteCategory(5) } returns true

        viewModel = CatalogoViewModel(mockRepository)
        viewModel.deleteCategory(5)
        advanceUntilIdle()

        coVerify { mockRepository.deleteCategory(5) }
        assertEquals("Categoría eliminada", viewModel.operationMessage.value)
    }
}