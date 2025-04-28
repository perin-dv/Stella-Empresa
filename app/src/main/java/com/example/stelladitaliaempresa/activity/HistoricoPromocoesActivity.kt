package com.example.stelladitaliaempresa.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stelladitaliaempresa.adapter.HistoricoPromocaoAdapter
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.ActivityHistoricoPromocoesBinding
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.app.AlertDialog
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.google.firebase.database.FirebaseDatabase

class HistoricoPromocoesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoricoPromocoesBinding
    private lateinit var adapter: HistoricoPromocaoAdapter
    private val listaPromocoes = mutableListOf<PromocaoEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricoPromocoesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar com botão de voltar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        // Configura Adapter e RecyclerView
        adapter = HistoricoPromocaoAdapter(listaPromocoes) { promocao ->
            confirmarExclusao(promocao)
        }

        binding.recyclerPromocoes.layoutManager = LinearLayoutManager(this)
        binding.recyclerPromocoes.adapter = adapter

        // Botão "Nova Promoção"
        binding.btnNovaPromocao.setOnClickListener {
            startActivity(Intent(this, PromocaoActivity::class.java))
        }

        // Carrega promoções
        buscarPromocoesFirebase()

    }

    override fun onResume() {
        super.onResume()

        buscarPromocoesFirebase() // Garante que promoções apareçam ao voltar
    }

    private fun buscarPromocoesFirebase() {
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child("7a3118oNdgcpmwSqrgyRTqBnFFx2")
            .child("promocoes")

        databaseRef.get().addOnSuccessListener { snapshot ->
            val lista = mutableListOf<PromocaoEntity>()
            snapshot.children.forEach { childSnapshot ->
                val id = childSnapshot.child("id").getValue(String::class.java) ?: ""
                val titulo = childSnapshot.child("titulo").getValue(String::class.java) ?: ""
                val observacao = childSnapshot.child("observacao").getValue(String::class.java) ?: ""
                val valor = childSnapshot.child("valor").getValue(Double::class.java) ?: 0.0
                val imagemBase64 = childSnapshot.child("imagemBase64").getValue(String::class.java) ?: ""
                val idUsuario = childSnapshot.child("idUsuario").getValue(String::class.java) ?: ""

                val produtosList = mutableListOf<ProdutoEntity>()
                childSnapshot.child("produtos").children.forEach { produtoSnapshot ->
                    val value = produtoSnapshot.value
                    when (value) {
                        is String -> {
                            produtosList.add(ProdutoEntity(id = value))
                        }
                        is HashMap<*, *> -> {
                            val produtoCompleto = ProdutoEntity(
                                id = value["id"] as? String ?: "",
                                nome = value["nome"] as? String ?: "",
                                preco = (value["preco"] as? Number)?.toDouble() ?: 0.0,
                                imagem = value["imagem"] as? String
                            )
                            produtosList.add(produtoCompleto)
                        }
                    }
                }

                val promocao = PromocaoEntity(
                    id = id,
                    titulo = titulo,
                    observacao = observacao,
                    valor = valor,
                    imagemBase64 = imagemBase64,
                    idUsuario = idUsuario,
                    produtos = produtosList
                )

                lista.add(promocao)
            }

            listaPromocoes.clear()
            listaPromocoes.addAll(lista)
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

    private fun confirmarExclusao(promocao: PromocaoEntity) {
        AlertDialog.Builder(this)
            .setTitle("Excluir promoção")
            .setMessage("Tem certeza que deseja excluir essa promoção?")
            .setPositiveButton("Sim") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    // Deleta do Room
                    AppDatabase.getInstance(this@HistoricoPromocoesActivity)
                        .promocaoDao()
                        .deleteById(promocao.id)

                    // Deleta do Firebase também!
                    val databaseRef = FirebaseDatabase.getInstance()
                        .getReference("empresa")
                        .child("7a3118oNdgcpmwSqrgyRTqBnFFx2")
                        .child("promocoes")
                        .child(promocao.id)

                    databaseRef.removeValue().addOnCompleteListener {
                        lifecycleScope.launch(Dispatchers.Main) {
                            buscarPromocoesFirebase() // Atualiza lista na tela
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

}
