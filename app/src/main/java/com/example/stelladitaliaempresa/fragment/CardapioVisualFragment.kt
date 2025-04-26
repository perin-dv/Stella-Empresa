package com.example.stelladitaliaempresa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.FragmentCardapioVisualBinding
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.adapter.CardapioAgrupadoAdapter
import com.example.stelladitaliaempresa.model.ItemCardapio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardapioVisualFragment : Fragment() {

    private var _binding: FragmentCardapioVisualBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CardapioAgrupadoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = FragmentCardapioVisualBinding.inflate(inflater, container, false).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        loadFromRoom()
    }

    override fun onResume() {
        super.onResume()
        loadFromRoom() // garante atualização ao voltar
    }

    private fun setupRecycler() {
        adapter = CardapioAgrupadoAdapter(
            requireContext(),
            emptyList(),
            object : CardapioAgrupadoAdapter.AbrirDialogEdicao {
                override fun onEditar(produtoItem: ItemCardapio.ProdutoItem) {
                    // sem edição aqui
                }
            }
        ) { /* clique opcional */ }

        binding.recyclerCardapio.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCardapio.adapter = adapter
    }

    private fun loadFromRoom() {
        lifecycleScope.launch {
            val dao = AppDatabase.getInstance(requireContext()).produtoDao()
            val produtos: List<ProdutoEntity> = withContext(Dispatchers.IO) { dao.getTodos() }
            val agrupada = produtos
                .groupBy { it.categoria ?: "Outros" }
                .flatMap { (cat, prods) ->
                    listOf(ItemCardapio.TituloCategoria(cat)) +
                            prods.map { ItemCardapio.ProdutoItem(it) }
                }
            adapter.atualizarLista(agrupada)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
