package com.example.stelladitaliaempresa.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stelladitaliaempresa.adapter.PedidoAdapter
import com.example.stelladitaliaempresa.databinding.FragmentPedidosBinding
import com.example.stelladitaliaempresa.model.Pedido
import com.google.firebase.database.*


class PedidosFragment : Fragment() {

    private var _binding: FragmentPedidosBinding? = null
    private val binding get() = _binding!!

    private val pedidos = mutableListOf<Pedido>()
    private lateinit var pedidosAdapter: PedidoAdapter
    private lateinit var pedidosRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPedidosBinding.inflate(inflater, container, false)

        pedidosRef = FirebaseDatabase.getInstance().getReference("pedidos")

        pedidosAdapter = PedidoAdapter(pedidos) { pedido ->
            aceitarPedido(pedido)
        }

        binding.recyclerPedidosPendentes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pedidosAdapter
        }

        recuperarPedidos()

        return binding.root
    }

    private fun recuperarPedidos() {
        pedidosRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                pedidos.clear()
                for (snap in snapshot.children) {
                    val pedido = snap.getValue(Pedido::class.java)
                    pedido?.let { pedidos.add(it) }
                }
                pedidosAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Erro ao carregar pedidos", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun aceitarPedido(pedido: Pedido) {
        pedido.status = "aceito"
        val pedidoRef = pedidosRef.child(pedido.id ?: return)

        pedidoRef.setValue(pedido)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Pedido aceito!", Toast.LENGTH_SHORT).show()
                pedidosAdapter.notifyDataSetChanged() // Atualiza o botão na lista
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao aceitar pedido", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
