package com.example.stelladitaliaempresa.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuracao")
data class ConfiguracaoEntity(
    @PrimaryKey val idUsuario: String,
    val taxaEntrega: String,
    val tempoEntrega: String
)


