package com.example.stelladitaliaempresa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.adapter.CardapioAgrupadoAdapter
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.FragmentGerenciarCardapioBinding
import com.example.stelladitaliaempresa.dialog.EditarProdutoDialogFragment
import com.example.stelladitaliaempresa.helper.UsuarioFirebase
import com.example.stelladitaliaempresa.model.ItemCardapio
import com.google.firebase.database.*

class CardapioFragment : Fragment() {

    private var _binding: FragmentGerenciarCardapioBinding? = null
    private val binding get() = _binding!!

    private val listaProdutos = mutableListOf<ProdutoEntity>()
    private lateinit var adapter: CardapioAgrupadoAdapter
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentGerenciarCardapioBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()

        // 1) Monta o Recycler
        adapter = CardapioAgrupadoAdapter(
            requireContext(),
            emptyList(),
            object : CardapioAgrupadoAdapter.AbrirDialogEdicao {
                override fun onEditar(produtoItem: ItemCardapio.ProdutoItem) {
                    EditarProdutoDialogFragment(produtoItem.produto) {
                        escutarProdutos()  // recarrega após edição
                    }.show(parentFragmentManager, "EditarProduto")
                }
            }
        ) { produtoItem ->
            // clicar no item também abre o diálogo de edição
            EditarProdutoDialogFragment(produtoItem.produto) {
                escutarProdutos()
            }.show(parentFragmentManager, "EditarProduto")
        }

        binding.recyclerGerenciar.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerGerenciar.adapter = adapter

        // 2) Começa a ouvir o RTDB
        escutarProdutos()
    }

    private fun setupRecycler() {
        adapter = CardapioAgrupadoAdapter(
            requireContext(),
            emptyList(),
            object : CardapioAgrupadoAdapter.AbrirDialogEdicao {
                override fun onEditar(item: ItemCardapio.ProdutoItem) {
                    EditarProdutoDialogFragment(item.produto) {
                        fetchFromFirebase()
                    }.show(parentFragmentManager, "EditarProduto")
                }
            },
            onClick = { item ->
                // também abre edição ao clicar no card
                EditarProdutoDialogFragment(item.produto) {
                    fetchFromFirebase()
                }.show(parentFragmentManager, "EditarProduto")
            }
        )
        binding.recyclerGerenciar.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerGerenciar.adapter = adapter
    }

    private fun escutarProdutos() {
        val empresaKey = UsuarioFirebase.getIdUsuario()
            .replace(".", "_dot_")
            .replace("@", "_at_")

        databaseRef = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(empresaKey)
            .child("produtos")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProdutos.clear()

                // Cada filho é uma categoria, que contém vários produtos
                for (categoriaSnap in snapshot.children) {
                    val categoria = categoriaSnap.key ?: "Outros"
                    for (prodSnap in categoriaSnap.children) {
                        prodSnap.getValue(ProdutoEntity::class.java)
                            ?.apply { this.categoria = categoria }
                            ?.let { listaProdutos.add(it) }
                    }
                }

                // Agrupa por categoria para o adapter
                val listaAgrupada = listaProdutos
                    .groupBy { it.categoria ?: "Outros" }
                    .flatMap { (cat, prods) ->
                        listOf(ItemCardapio.TituloCategoria(cat)) +
                                prods.map { ItemCardapio.ProdutoItem(it) }
                    }

                adapter.atualizarLista(listaAgrupada)
            }

            override fun onCancelled(error: DatabaseError) {
                // opcional: Log.e("CardapioFragment", "falhou: ${error.message}")
            }
        })
    }
    private fun fetchFromFirebase() {
        // sua lógica atual de addValueEventListener...
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProdutos.clear()
                snapshot.children.forEach {
                    it.getValue(ProdutoEntity::class.java)?.let { listaProdutos.add(it) }
                }
                val agrupada = listaProdutos
                    .groupBy { it.categoria ?: "Outros" }
                    .flatMap { (cat, prods) ->
                        listOf(ItemCardapio.TituloCategoria(cat)) +
                                prods.map { ItemCardapio.ProdutoItem(it) }
                    }
                adapter.atualizarLista(agrupada)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
