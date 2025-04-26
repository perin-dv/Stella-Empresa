package com.example.stelladitaliaempresa.model

data class Pedido(
    var id: String? = null,
    var nomeCliente: String? = null,
    var descricao: String? = null,
    var status: String? = "pendente", // pode ser "pendente", "aceito"
    val total: Double = 0.0
)
