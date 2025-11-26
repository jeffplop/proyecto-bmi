package com.example.backend_bmi.service

import com.example.backend_bmi.model.Category
import com.example.backend_bmi.repository.CategoryRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Arrays

@ExtendWith(MockitoExtension::class)
class CategoryServiceTest {
    @Mock
    private val categoryRepository: CategoryRepository? = null

    @InjectMocks
    private val categoryService: CategoryService? = null

    @Test
    fun testObtenerTodasLasCategorias() {
        val cat1: Category = Category(1, "Bluetooth", "Conexión inalámbrica")
        val cat2: Category = Category(2, "Indicadores", "Pantallas")

        Mockito.`when`(categoryRepository.findAll()).thenReturn(Arrays.asList<T?>(cat1, cat2))

        val resultados: MutableList<Category?> = categoryService.obtenerTodas()

        Assertions.assertEquals(2, resultados.size)
        Assertions.assertEquals("Bluetooth", resultados.get(0).getNombre())
    }

    @Test
    fun testGuardarCategoria() {
        val nueva: Category = Category(null, "Impresoras", "Equipos")
        val guardada: Category = Category(3, "Impresoras", "Equipos")

        Mockito.`when`(categoryRepository.save(any(Category::class.java))).thenReturn(guardada)

        val resultado: Category = categoryService.guardarCategoria(nueva)

        Assertions.assertEquals(3, resultado.getId())
        Assertions.assertEquals("Impresoras", resultado.getNombre())
    }

    @Test
    fun testEliminarCategoriaExistente() {
        // Simulamos que la categoría ID 1 existe
        Mockito.`when`(categoryRepository.existsById(1)).thenReturn(true)

        val eliminado: Boolean = categoryService.eliminarCategoria(1)

        Assertions.assertTrue(eliminado)
        // Verificamos que se llamó al método deleteById del repositorio
        verify(categoryRepository, times(1)).deleteById(1)
    }

    @Test
    fun testEliminarCategoriaNoExistente() {
        // Simulamos que la categoría ID 99 NO existe
        Mockito.`when`(categoryRepository.existsById(99)).thenReturn(false)

        val eliminado: Boolean = categoryService.eliminarCategoria(99)

        Assertions.assertFalse(eliminado)
        // Verificamos que NUNCA se llamó a deleteById
        verify(categoryRepository, times(0)).deleteById(99)
    }
}