package com.ctma.prestamolab.data

import com.ctma.prestamolab.data.repository.AuthRepository
import com.ctma.prestamolab.data.repository.InMemoryAuthRepository
import com.ctma.prestamolab.data.repository.InMemoryPrestamoRepository
import com.ctma.prestamolab.data.repository.PrestamoRepository

object ServiceLocator {
    val repository: PrestamoRepository by lazy {
        InMemoryPrestamoRepository()
    }
    
    val authRepository: AuthRepository by lazy {
        InMemoryAuthRepository()
    }
}
