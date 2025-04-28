package com.example.stelladitaliaempresa.util

import androidx.room.TypeConverter
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromProdutoList(value: List<ProdutoEntity>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toProdutoList(value: String): List<ProdutoEntity> {
        val listType = object : TypeToken<List<ProdutoEntity>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
