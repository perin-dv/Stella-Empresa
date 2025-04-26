package com.example.stelladitaliaempresa.adpter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.databinding.ItemCardapioEditarBinding
import com.example.stelladitaliaempresa.imageutil.ImageUtils.loadBase64IntoImageView

class ProdutoGerenciarAdapter(
    private val context: Context,
    private val produtos: List<ProdutoEntity>,
    private val listener: OnProdutoActionListener
) : RecyclerView.Adapter<ProdutoGerenciarAdapter.MyViewHolder>() {

    interface OnProdutoActionListener {
        fun onEditar(produto: ProdutoEntity)
        fun onExcluir(produto: ProdutoEntity)
    }

    inner class MyViewHolder(val binding: ItemCardapioEditarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = ItemCardapioEditarBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val produto = produtos[position]
        val binding = holder.binding

        binding.txtNomeProduto.text = produto.nome
        binding.txtPrecoProduto.text = "R$ ${String.format("%.2f", produto.preco)}"

        // Carrega imagem em Base64
        loadBase64IntoImageView(produto.imagem, binding.imgProduto)

        // Clique editar
        binding.btnEditar.setOnClickListener {
            listener.onEditar(produto)
        }

        // Clique excluir
        binding.btnExcluir.setOnClickListener {
            listener.onExcluir(produto)
        }
    }

    override fun getItemCount(): Int = produtos.size
}
