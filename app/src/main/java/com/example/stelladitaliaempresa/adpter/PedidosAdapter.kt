package com.example.stelladitaliaempresa.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stelladitaliaempresa.databinding.ItemPedidoBinding
import com.example.stelladitaliaempresa.model.Pedido

class PedidoAdapter(
    private val listaPedidos: List<Pedido>,
    private val onClickAceitar: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemPedidoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val pedido = listaPedidos[position]
        holder.binding.textNomeCliente.text = "Cliente: ${pedido.nomeCliente}"
        holder.binding.textDescricaoPedido.text = "Pedido: ${pedido.descricao}"
        holder.binding.buttonAceitar.setOnClickListener {
            onClickAceitar(pedido)
        }
    }

    override fun getItemCount(): Int = listaPedidos.size
}
