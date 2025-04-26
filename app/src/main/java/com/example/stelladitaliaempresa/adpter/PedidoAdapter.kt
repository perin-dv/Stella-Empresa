package com.example.stelladitaliaempresa.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stelladitaliaempresa.databinding.ItemPedidoBinding
import com.example.stelladitaliaempresa.model.Pedido

class PedidoAdapter(
    private val context: Context,
    private val pedidos: List<Pedido>,
    private val onClick: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(val binding: ItemPedidoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pedido: Pedido) {
            binding.textNomeCliente.text = pedido.nomeCliente
            binding.textDescricaoPedido.text = pedido.status
            binding.totalPedido.text = "Total: R$ %.2f".format(pedido.total)

            // Clique no item
            binding.root.setOnClickListener {
                onClick(pedido)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPedidoBinding.inflate(layoutInflater, parent, false)
        return PedidoViewHolder(binding)
    }

    override fun getItemCount() = pedidos.size

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        holder.bind(pedidos[position])
    }
}
