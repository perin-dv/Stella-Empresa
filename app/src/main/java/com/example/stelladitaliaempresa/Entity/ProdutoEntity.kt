package com.example.stelladitaliaempresa.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produtos")
data class ProdutoEntity(
    @PrimaryKey(autoGenerate = true)
    val idLocal: Int = 0,
    var id: String? = null,
    var nome: String = "",
    var descricao: String = "",
    var preco: Double = 0.0,
    var imagem: String? = null, // ✅ este é o nome correto
    var idUsuario: String = "",
    var categoria: String? = null
)
