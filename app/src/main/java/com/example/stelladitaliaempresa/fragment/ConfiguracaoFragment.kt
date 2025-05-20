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
import com.example.stelladitaliaempresa.databinding.FragmentConfiguracoesBinding
import com.example.stelladitaliaempresa.dialog.DialogVendasDia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ConfiguracaoFragment : Fragment() {

    private var _binding: FragmentConfiguracoesBinding? = null
    private val binding get() = _binding ?: error("Binding inválido.")

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracoesBinding.inflate(inflater, container, false)

        configurarUI()
        recuperarConfiguracoes()

        return binding.root
    }

    private fun configurarUI() {
        binding.btnSalvarDados.setOnClickListener { salvarConfiguracoes() }
        binding.btnPromocoes.setOnClickListener {
            startActivity(Intent(requireContext(), HistoricoPromocoesActivity::class.java))
        }
        binding.btnSair.setOnClickListener { deslogarUsuario(requireContext()) }
        binding.btnVerVendas.setOnClickListener {
            DialogVendasDia(
                context = requireContext(),
                totalPedidos = 0,
                entregues = 0,
                cancelados = 0,
                faturamento = 0.0
            ).show()
        }

        binding.toolbar.apply {
            title = "Configurações"
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun salvarConfiguracoes() {
        val taxaEntregaStr = binding.editTaxaEntrega.text.toString().trim()
        val tempoEntregaStr = binding.editTempoEntrega.text.toString().trim()

        // 🧠 Pegue o UID correto da empresa usado pelo app cliente
        val uidEmpresa = "7a3118oNdgcpmwSqrgyRTqBnFFx2" // <-- valor fixo ou vindo de config

        val taxaEntrega = taxaEntregaStr.toDoubleOrNull()
        val tempoEntrega = tempoEntregaStr.toIntOrNull()

        if (taxaEntrega == null || tempoEntrega == null) {
            Toast.makeText(requireContext(), "Preencha valores válidos", Toast.LENGTH_SHORT).show()
            return
        }

        val dados = mapOf(
            "taxaEntrega" to taxaEntrega,
            "tempoEntrega" to tempoEntrega
        )

        FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(uidEmpresa)
            .child("config")
            .setValue(dados)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Configurações salvas com sucesso!", Toast.LENGTH_SHORT).show()
                    Log.d("CONFIG_EMPRESA", "✅ Salvo em empresa/$uidEmpresa/config")
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar configurações", Toast.LENGTH_SHORT).show()
                    Log.e("CONFIG_EMPRESA", "❌ Falha: ${task.exception?.message}")
                }
            }
    }


    private fun recuperarConfiguracoes() {
        val uidEmpresa = "7a3118oNdgcpmwSqrgyRTqBnFFx2" // <- mesmo UID usado para salvar

        val referencia = FirebaseDatabase.getInstance()
            .getReference("empresa")
            .child(uidEmpresa)
            .child("config")

        referencia.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rawTaxa = snapshot.child("taxaEntrega").value
                val rawTempo = snapshot.child("tempoEntrega").value

                val taxaEntrega = when (rawTaxa) {
                    is Long -> rawTaxa.toString()
                    is Double -> rawTaxa.toString()
                    is String -> rawTaxa
                    else -> ""
                }

                val tempoEntrega = when (rawTempo) {
                    is Long -> rawTempo.toString()
                    is Double -> rawTempo.toInt().toString()
                    is String -> rawTempo
                    else -> ""
                }

                binding.editTaxaEntrega.setText(taxaEntrega)
                binding.editTempoEntrega.setText(tempoEntrega)

                Log.d("CONFIG_EMPRESA", "✅ Carregado: taxa=$taxaEntrega, tempo=$tempoEntrega")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CONFIG_EMPRESA", "❌ Erro ao carregar: ${error.message}")
            }
        })
    }



    private fun deslogarUsuario(context: Context) {
        auth.signOut()
        val intent = Intent(context, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
