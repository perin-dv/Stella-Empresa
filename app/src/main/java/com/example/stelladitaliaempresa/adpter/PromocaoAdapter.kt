package com.example.stelladitaliaempresa.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import com.example.stelladitaliaempresa.databinding.ItemHistoricoPromocaoBinding


class PromocaoAdapter(
    private val onDeleteClick: (PromocaoEntity) -> Unit
) : RecyclerView.Adapter<PromocaoAdapter.PromocaoViewHolder>() {

    private var listaPromocoes = listOf<PromocaoEntity>()

    fun submitList(lista: List<PromocaoEntity>) {
        listaPromocoes = lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromocaoViewHolder {
        val binding = ItemHistoricoPromocaoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PromocaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromocaoViewHolder, position: Int) {
        holder.bind(listaPromocoes[position])
    }

    override fun getItemCount(): Int = listaPromocoes.size

    inner class PromocaoViewHolder(private val binding: ItemHistoricoPromocaoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(promocao: PromocaoEntity) {
            binding.txtTituloPromocao.text = promocao.titulo ?: "Sem título"

            binding.btnExcluirPromocao.setOnClickListener {
                onDeleteClick(promocao)
            }
        }
    }
}
