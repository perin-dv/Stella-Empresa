package com.example.stelladitaliaempresa.ui.home.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.apkstelladitalia20.activity.AuthenticationActivity
import com.example.stelladitaliaempresa.R
import com.example.stelladitaliaempresa.activity.HistoricoPromocoesActivity
import com.example.stelladitaliaempresa.activity.PromocaoActivity
import com.example.stelladitaliaempresa.databinding.FragmentConfiguracoesBinding
import com.example.stelladitaliaempresa.dialog.DialogVendasDia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ConfiguracaoFragment : Fragment() {

    private var _binding: FragmentConfiguracoesBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding só pode ser acessado entre onCreateView e onDestroyView.")

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val idUsuario get() = auth.currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracoesBinding.inflate(inflater, container, false)

        binding.btnSalvarDados.setOnClickListener {
            salvarConfiguracoes()
        }

        binding.btnPromocoes.setOnClickListener {
            Toast.makeText(requireContext(), "Ir para Promoções", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), HistoricoPromocoesActivity::class.java))
        }

        binding.btnSair.setOnClickListener {
            deslogarUsuario(requireContext())
        }

        binding.btnVerVendas.setOnClickListener {
            Toast.makeText(requireContext(), "Vendas do dia", Toast.LENGTH_SHORT).show()
            val dialog = DialogVendasDia(
                context = requireContext(),
                totalPedidos = 0,
                entregues = 0,
                cancelados = 0,
                faturamento = 0.0
            )
            dialog.show()
        }

        (binding.toolbar).apply {
            title = "Configurações"
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        recuperarConfiguracoes()

        return binding.root
    }

    private fun salvarConfiguracoes() {
        val taxaEntrega = binding.editTaxaEntrega.text.toString()
        val tempoEntrega = binding.editTempoEntrega.text.toString()

        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid

        if (!idUsuario.isNullOrBlank()) {
            val referencia = FirebaseDatabase.getInstance()
                .getReference("empresa")
                .child(idUsuario)
                .child("config") // ✅ ESSENCIAL: salvar dentro de "config"

            val dados = mapOf(
                "taxaEntrega" to taxaEntrega,
                "tempoEntrega" to tempoEntrega
            )

            referencia.updateChildren(dados)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Configuração salva com sucesso!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Erro ao salvar", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }


    private fun recuperarConfiguracoes() {
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val referencia = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(idUsuario)
            .child("config")

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taxa = snapshot.child("taxaEntrega").value?.toString() ?: ""
                val tempo = snapshot.child("tempoEntrega").value?.toString() ?: ""

                binding.editTaxaEntrega.setText(taxa)
                binding.editTempoEntrega.setText(tempo)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Empresa", "Erro ao buscar configurações: ${error.message}")
            }
        })
    }


    private fun deslogarUsuario(context: Context) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
