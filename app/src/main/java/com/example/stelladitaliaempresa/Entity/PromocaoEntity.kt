package com.example.stelladitaliaempresa.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promocoes")
data class PromocaoEntity(
    @PrimaryKey
    var id: String = "",

    val titulo: String = "",

    val valor: Double = 0.0,

    val observacao: String = "",

    val produtos: List<String>? = emptyList(),

    val imagemBase64: String? = null,

    val idUsuario: String = "",

    val nomeUsuario: String? = null
)
