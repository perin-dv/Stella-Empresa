package com.example.stelladitaliaempresa.model

import com.example.stelladitaliaempresa.Entity.ProdutoEntity

data class ItemLista(
    val isHeader: Boolean,
    val categoria: String? = null,
    val produto: ProdutoEntity? = null
)
