package com.example.stelladitaliaempresa.adapter

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import com.example.stelladitaliaempresa.R

class HistoricoPromocaoAdapter(
    private val lista: MutableList<PromocaoEntity>,
    private val onExcluirClick: (PromocaoEntity) -> Unit
) : RecyclerView.Adapter<HistoricoPromocaoAdapter.PromocaoViewHolder>() {

    inner class PromocaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txtTituloPromocao)
        val imgPromocao: ImageView = itemView.findViewById(R.id.imgPromocaoCard)
        val btnExcluir: ImageButton = itemView.findViewById(R.id.btnExcluirPromocao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromocaoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historico_promocao, parent, false)
        return PromocaoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PromocaoViewHolder, position: Int) {
        val promocao = lista[position]

        // Título
        holder.txtTitulo.text = promocao.titulo

        // Imagem
        if (!promocao.imagemBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(promocao.imagemBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.imgPromocao.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            holder.imgPromocao.setImageResource(R.drawable.ic_sem_imagem) // imagem padrão
        }

        // Excluir
        holder.btnExcluir.setOnClickListener {
            onExcluirClick(promocao)
        }

    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<PromocaoEntity>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }
}
