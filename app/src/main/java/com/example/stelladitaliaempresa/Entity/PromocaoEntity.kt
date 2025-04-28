package com.example.stelladitaliaempresa.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.stelladitaliaempresa.util.Converters

@Entity(tableName = "promocoes")
data class PromocaoEntity(
    @PrimaryKey
    val id: String = "",
    val titulo: String = "",
    val valor: Double = 0.0,
    val observacao: String = "",
    val produtos: List<ProdutoEntity> = emptyList(), // ✅ agora é emptyList()
    val imagemBase64: String? = null,
    val idUsuario: String = "",
    val nomeUsuario: String? = null
)
