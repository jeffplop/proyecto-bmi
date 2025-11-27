package com.example.proyecto_bmi.repository

import com.example.proyecto_bmi.data.local.dao.UsuarioDao
import com.example.proyecto_bmi.data.local.entity.UsuarioEntity
import com.example.proyecto_bmi.data.local.repository.UsuarioRepository
import com.example.proyecto_bmi.data.remote.api.ApiService
import com.example.proyecto_bmi.data.remote.api.RetrofitInstance
import com.example.proyecto_bmi.data.remote.model.UserRemote
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class UsuarioRepositoryTest : StringSpec({

    val mockDao = mockk<UsuarioDao>(relaxed = true)
    val mockApi = mockk<ApiService>()
    val repository = UsuarioRepository(mockDao)

    beforeTest {
        mockkObject(RetrofitInstance)
        coEvery { RetrofitInstance.api } returns mockApi
    }

    afterTest {
        unmockkAll()
    }

    "autenticarUsuario debe retornar usuario valido si API responde correctamente" {
        runTest {
            val email = "test@bmi.cl"
            val clave = "123"

            val userRemoto = UserRemote(
                id = 1,
                nombre = "Test User",
                email = email,
                password = clave,
                telefono = "+569",
                role = "USER"
            )

            coEvery { mockApi.login(any()) } returns userRemoto
            coEvery { mockDao.insertUser(any()) } returns 1L

            val resultado = repository.autenticarUsuario(email, clave)

            resultado?.email shouldBe email
            coVerify { mockDao.insertUser(any()) }
        }
    }

    "autenticarUsuario debe buscar en local si la API falla" {
        runTest {
            coEvery { mockApi.login(any()) } throws Exception("Error de red")

            val userLocal = UsuarioEntity(1, "Local", "local@bmi.cl", "123", "+569")
            coEvery { mockDao.getUserByCredentials(any(), any()) } returns userLocal

            val resultado = repository.autenticarUsuario("local@bmi.cl", "123")

            resultado?.nombre shouldBe "Local"
        }
    }
})