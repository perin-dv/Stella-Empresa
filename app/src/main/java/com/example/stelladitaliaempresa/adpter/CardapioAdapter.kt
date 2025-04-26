package com.example.stelladitaliaempresa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stelladitaliaempresa.databinding.ItemProdutoCardapioVisualBinding
import com.example.stelladitaliaempresa.databinding.ItemTituloCategoriaBinding
import com.example.stelladitaliaempresa.imageutil.ImageUtils
import com.example.stelladitaliaempresa.model.ItemCardapio

class CardapioAgrupadoAdapter(
    private val context: Context,
    private var lista: List<ItemCardapio>,
    private val listener: AbrirDialogEdicao,
    private val onClick: (produto: ItemCardapio.ProdutoItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TIPO_CATEGORIA = 0
        private const val TIPO_PRODUTO = 1
    }

    interface AbrirDialogEdicao {
        fun onEditar(produtoItem: ItemCardapio.ProdutoItem)
    }

    inner class TituloViewHolder(val binding: ItemTituloCategoriaBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ProdutoViewHolder(val binding: ItemProdutoCardapioVisualBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (lista[position]) {
            is ItemCardapio.TituloCategoria -> TIPO_CATEGORIA
            is ItemCardapio.ProdutoItem -> TIPO_PRODUTO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TIPO_CATEGORIA -> {
                val binding = ItemTituloCategoriaBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                TituloViewHolder(binding)
            }
            else -> {
                val binding = ItemProdutoCardapioVisualBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ProdutoViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = lista[position]) {
            is ItemCardapio.TituloCategoria -> {
                val viewHolder = holder as TituloViewHolder
                viewHolder.binding.txtTituloCategoria.text = item.categoria
            }
            is ItemCardapio.ProdutoItem -> {
                val viewHolder = holder as ProdutoViewHolder
                val produto = item.produto

                viewHolder.binding.textNome.text = produto.nome
                viewHolder.binding.textDescricao.text = produto.descricao
                viewHolder.binding.textPreco.text = "R$ %.2f".format(produto.preco ?: 0.0)

                produto.imagem?.let {
                    ImageUtils.loadBase64IntoImageView(it, viewHolder.binding.imageProduto)
                } ?: viewHolder.binding.imageProduto.setImageResource(com.example.stelladitaliaempresa.R.drawable.ic_sem_imagem)

                val contextName = context::class.java.simpleName
                viewHolder.binding.btnEditar.visibility =
                    if (contextName.contains("Home")) View.GONE else View.VISIBLE

                viewHolder.binding.btnEditar.setOnClickListener {
                    listener.onEditar(item)
                }

                viewHolder.binding.root.setOnClickListener {
                    onClick(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<ItemCardapio>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}
