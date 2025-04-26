
package com.example.stelladitaliaempresa.model

import com.example.stelladitaliaempresa.Entity.ProdutoEntity

sealed class ItemCardapio {
    data class TituloCategoria(val categoria: String) : ItemCardapio()
    data class ProdutoItem(val produto: ProdutoEntity) : ItemCardapio()
}
