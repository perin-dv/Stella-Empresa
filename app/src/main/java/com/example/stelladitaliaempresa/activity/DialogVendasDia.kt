package com.example.stelladitaliaempresa.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.stelladitaliaempresa.databinding.DialogVendasDiaBinding

@SuppressLint("SetTextI18n")
class DialogVendasDia(
    context: Context,
    totalPedidos: Int,
    entregues: Int,
    cancelados: Int,
    faturamento: Double
) {
    private val dialog = Dialog(context)
    private val binding = DialogVendasDiaBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Preenchendo os dados
        binding.txtTotalPedidos.text = "Pedidos: $totalPedidos"
        binding.txtEntregues.text = "Entregues: $entregues"
        binding.txtCancelados.text = "Cancelados: $cancelados"
        binding.txtFaturamento.text = "Faturamento: R$ %.2f".format(faturamento)

        binding.txtResumo.text = when {
            faturamento >= 300 -> "🎉 Você teve um excelente dia de vendas!"
            faturamento >= 100 -> "Bom trabalho! Continue assim 💪"
            else -> "Hoje foi fraco... vamos melhorar amanhã 😉"
        }

        binding.btnFechar.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun show() = dialog.show()
}
