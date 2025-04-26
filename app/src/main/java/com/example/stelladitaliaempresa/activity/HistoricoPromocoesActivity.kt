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
        carregarPromocoes()
    }

    override fun onResume() {
        super.onResume()
        carregarPromocoes() // Garante que promoções apareçam ao voltar
    }

    private fun carregarPromocoes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val promocoes = AppDatabase.getInstance(this@HistoricoPromocoesActivity)
                .promocaoDao()
                .getAll()

            withContext(Dispatchers.Main) {
                adapter.atualizarLista(promocoes)
            }
        }
    }

    private fun confirmarExclusao(promocao: PromocaoEntity) {
        AlertDialog.Builder(this)
            .setTitle("Excluir promoção")
            .setMessage("Tem certeza que deseja excluir essa promoção?")
            .setPositiveButton("Sim") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getInstance(this@HistoricoPromocoesActivity)
                        .promocaoDao()
                        .deleteById(promocao.id)

                    withContext(Dispatchers.Main) {
                        carregarPromocoes()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
