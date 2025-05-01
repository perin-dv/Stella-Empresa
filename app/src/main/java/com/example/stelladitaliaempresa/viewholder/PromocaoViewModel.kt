package com.example.stelladitaliaempresa.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import com.example.stelladitaliaempresa.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PromocaoViewModel (private val context: Context): ViewModel() {

    suspend fun salvar(promocao: PromocaoEntity) {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(context).promocaoDao()
            dao.insert(promocao)
        }
    }
}
