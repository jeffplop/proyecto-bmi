package com.example.proyecto_bmi.viewmodel

import com.example.proyecto_bmi.data.remote.model.CategoryRemote
import com.example.proyecto_bmi.data.repository.CatalogoRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkConstructor
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

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns dispatcher
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

        mockkConstructor(CatalogoRepository::class)
        coEvery { anyConstructed<CatalogoRepository>().getCategories() } returns fakeCategories

        val viewModel = CatalogoViewModel()
        advanceUntilIdle()

        assertEquals(2, viewModel.categories.value.size)
        assertEquals("Balanzas", viewModel.categories.value[0].nombre)
        assertEquals("Indicadores", viewModel.categories.value[1].nombre)
    }

    @Test
    fun fetchCategories_debe_manejar_lista_vacia() = runTest(dispatcher) {
        mockkConstructor(CatalogoRepository::class)
        coEvery { anyConstructed<CatalogoRepository>().getCategories() } returns emptyList()

        val viewModel = CatalogoViewModel()
        advanceUntilIdle()

        assertEquals(0, viewModel.categories.value.size)
    }
}