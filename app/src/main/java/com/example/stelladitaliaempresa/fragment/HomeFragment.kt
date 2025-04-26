package com.example.stelladitaliaempresa.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.adapter.CardapioAgrupadoAdapter
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.FragmentHomeBinding
import com.example.stelladitaliaempresa.helper.UsuarioFirebase
import com.example.stelladitaliaempresa.model.ItemCardapio
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // lista em memória de todos os produtos (independente de categoria)
    private val listaProdutos = mutableListOf<ProdutoEntity>()
    private lateinit var adapter: CardapioAgrupadoAdapter
    private lateinit var databaseRef: DatabaseReference
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDatabase = AppDatabase.getInstance(requireContext())
        configurarRecycler()
        escutarProdutos()
    }

    private fun configurarRecycler() {
        adapter = CardapioAgrupadoAdapter(
            requireContext(),
            emptyList(),
            object : CardapioAgrupadoAdapter.AbrirDialogEdicao {
                override fun onEditar(produtoItem: ItemCardapio.ProdutoItem) {
                    // mantém função de edição para futuras telas
                }
            }
        ) { /* clique no item, se quiser */ }

        binding.recyclerCardapio.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCardapio.adapter = adapter
    }

    private fun escutarProdutos() {
        // monta a chave da empresa (substituindo . e @)
        val empresaKey = UsuarioFirebase.getIdUsuario()
            .replace(".", "_dot_")
            .replace("@", "_at_")

        // usa nó singular "empresa" para _salvar_ e _ler_ produtos
        databaseRef = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(empresaKey)
            .child("produtos")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProdutos.clear()

                // percorre primeiro nível (categorias) e depois produtos dentro de cada categoria
                for (categoriaSnap in snapshot.children) {
                    for (produtoSnap in categoriaSnap.children) {
                        produtoSnap.getValue(ProdutoEntity::class.java)
                            ?.let { listaProdutos.add(it) }
                    }
                }

                // agrupa por categoria para o adapter
                val listaAgrupada = listaProdutos
                    .groupBy { it.categoria ?: "Outros" }
                    .flatMap { (categoria, produtos) ->
                        listOf(ItemCardapio.TituloCategoria(categoria)) +
                                produtos.map { ItemCardapio.ProdutoItem(it) }
                    }

                adapter.atualizarLista(listaAgrupada)
                salvarLocalmente(listaProdutos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Erro ao carregar produtos: ${error.message}")
            }
        })
    }

    /** Persiste toda a lista no Room */
    private fun salvarLocalmente(lista: List<ProdutoEntity>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = appDatabase.produtoDao()
            dao.deletarTodos()
            dao.insertAll(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
