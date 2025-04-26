package com.seuapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.databinding.ItemCategoriaHeaderBinding
import com.example.stelladitaliaempresa.databinding.ItemProdutoCardapioVisualBinding
import com.example.stelladitaliaempresa.imageutil.ImageUtils.loadBase64IntoImageView
import com.example.stelladitaliaempresa.model.ItemLista

class CategoriaProdutoAdapter(
    private val lista: List<ItemLista>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TIPO_HEADER = 0
        private const val TIPO_PRODUTO = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (lista[position].isHeader) TIPO_HEADER else TIPO_PRODUTO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TIPO_HEADER) {
            val binding = ItemCategoriaHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemProdutoCardapioVisualBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ProdutoViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = lista[position]
        if (holder is HeaderViewHolder) {
            holder.bind(item.categoria ?: "")
        } else if (holder is ProdutoViewHolder) {
            item.produto?.let { holder.bind(it) }
        }
    }

    override fun getItemCount(): Int = lista.size

    class HeaderViewHolder(private val binding: ItemCategoriaHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(nome: String) {
            binding.txtCategoria.text = nome
        }
    }

    class ProdutoViewHolder(private val binding: ItemProdutoCardapioVisualBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(produto: ProdutoEntity) {
            binding.textNome.text = produto.nome
            binding.textPreco.text = "R$ ${produto.preco}"
            binding.textDescricao.text = produto.descricao
            loadBase64IntoImageView(produto.imagem, binding.imageProduto)
        }
    }
}
