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
import com.example.stelladitaliaempresa.model.ItemCardapio
import com.google.firebase.database.*

class CardapioFragment : Fragment() {

    private var _binding: FragmentGerenciarCardapioBinding? = null
    private val binding get() = _binding!!

    private val listaProdutos = mutableListOf<ProdutoEntity>()
    private lateinit var adapter: CardapioAgrupadoAdapter
    private lateinit var databaseRef: DatabaseReference

    private val idEmpresa = "7a3118oNdgcpmwSqrgyRTqBnFFx2" // 🔥 ID fixo da empresa

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
                EditarProdutoDialogFragment(item.produto) {
                    fetchFromFirebase()
                }.show(parentFragmentManager, "EditarProduto")
            }
        )
        binding.recyclerGerenciar.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerGerenciar.adapter = adapter
    }

    private fun escutarProdutos() {
        databaseRef = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idEmpresa)
            .child("produtos")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProdutos.clear()

                for (produtoSnap in snapshot.children) {
                    produtoSnap.getValue(ProdutoEntity::class.java)?.let {
                        listaProdutos.add(it)
                    }
                }

                val listaAgrupada = listaProdutos
                    .groupBy { it.categoria ?: "Outros" }
                    .flatMap { (cat, prods) ->
                        listOf(ItemCardapio.TituloCategoria(cat)) +
                                prods.map { ItemCardapio.ProdutoItem(it) }
                    }

                adapter.atualizarLista(listaAgrupada)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchFromFirebase() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaProdutos.clear()

                for (produtoSnap in snapshot.children) {
                    produtoSnap.getValue(ProdutoEntity::class.java)?.let {
                        listaProdutos.add(it)
                    }
                }

                val listaAgrupada = listaProdutos
                    .groupBy { it.categoria ?: "Outros" }
                    .flatMap { (cat, prods) ->
                        listOf(ItemCardapio.TituloCategoria(cat)) +
                                prods.map { ItemCardapio.ProdutoItem(it) }
                    }

                adapter.atualizarLista(listaAgrupada)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
