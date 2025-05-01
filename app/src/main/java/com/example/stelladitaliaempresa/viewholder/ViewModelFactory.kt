package com.example.stelladitaliaempresa.viewholder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stelladitaliaempresa.viewmodel.PromocaoViewModel

class PromocaoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PromocaoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PromocaoViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
