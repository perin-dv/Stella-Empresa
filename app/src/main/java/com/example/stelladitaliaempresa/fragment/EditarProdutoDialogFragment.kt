package com.example.stelladitaliaempresa.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.data.AppDatabase
import com.example.stelladitaliaempresa.databinding.DialogEditarProdutoBinding
import com.example.stelladitaliaempresa.imageutil.ImageUtils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import java.io.IOException

class EditarProdutoDialogFragment(
    private val produto: ProdutoEntity,
    private val onAtualizar: () -> Unit
) : DialogFragment() {

    private var _binding: DialogEditarProdutoBinding? = null
    private val binding get() = _binding!!
    private var imagemBase64: String? = produto.imagem

    private val idEmpresa = "7a3118oNdgcpmwSqrgyRTqBnFFx2" // UID fixo da empresa

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditarProdutoBinding.inflate(layoutInflater)
        setupCategoriaSpinner()
        preencherCampos()

        binding.btnSalvar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar alterações")
                .setMessage("Deseja mesmo salvar as alterações?")
                .setPositiveButton("Sim") { _, _ -> salvarEdicao() }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.btnDeletar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Excluir Produto")
                .setMessage("Tem certeza que deseja excluir este produto?")
                .setPositiveButton("Sim") { _, _ -> excluirProduto() }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        binding.imgProdutoDialog.setOnClickListener {
            abrirGaleria()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupCategoriaSpinner() {
        val categorias = listOf(
            "Pizza Tradicional", "Pizza Especial", "Pizza Premium",
            "Pizza Vegetariana", "Pizza Doce", "Porções",
            "Combos", "Bebidas", "Bebidas sem álcool"
        )
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, categorias)
        binding.spinnerCategoriaDialog.setAdapter(adapter)
        binding.spinnerCategoriaDialog.threshold = 1
        binding.spinnerCategoriaDialog.setOnClickListener {
            binding.spinnerCategoriaDialog.showDropDown()
        }
    }

    private fun preencherCampos() {
        binding.editNome.setText(produto.nome)
        binding.editPreco.setText(produto.preco?.toString() ?: "")
        binding.editObservacao.setText(produto.descricao ?: "")
        binding.spinnerCategoriaDialog.setText(produto.categoria, false)
        ImageUtils.loadBase64IntoImageView(produto.imagem, binding.imgProdutoDialog)
    }

    private fun salvarEdicao() {
        val nome = binding.editNome.text.toString()
        val preco = binding.editPreco.text.toString().toDoubleOrNull()
        val descricao = binding.editObservacao.text.toString()
        val categoria = binding.spinnerCategoriaDialog.text.toString()

        if (nome.isEmpty() || preco == null || categoria.isEmpty()) {
            Toast.makeText(context, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            return
        }

        val atualizado = produto.copy(
            nome = nome,
            preco = preco,
            descricao = descricao,
            categoria = categoria,
            imagem = imagemBase64
        )

        CoroutineScope(Dispatchers.IO).launch {
            // 1) Atualiza localmente (Room)
            AppDatabase.getInstance(requireContext())
                .produtoDao()
                .update(atualizado)

            // 2) Atualiza no Firebase em empresa/7a3118oNdgcpmwSqrgyRTqBnFFx2/produtos/idProduto
            FirebaseDatabase.getInstance()
                .getReference("empresa")
                .child(idEmpresa)
                .child("produtos")
                .child(atualizado.id ?: "")
                .setValue(atualizado)

            withContext(Dispatchers.Main) {
                onAtualizar()
                Toast.makeText(requireContext(), "Alterações salvas", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun excluirProduto() {
        CoroutineScope(Dispatchers.IO).launch {
            // Remove no Firebase
            FirebaseDatabase.getInstance()
                .getReference("empresa")
                .child(idEmpresa)
                .child("produtos")
                .child(produto.id ?: "")
                .removeValue()

            // Remove no Room
            AppDatabase.getInstance(requireContext())
                .produtoDao()
                .delete(produto)

            withContext(Dispatchers.Main) {
                onAtualizar()
                Toast.makeText(requireContext(), "Produto excluído", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            uri?.let {
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media
                        .getBitmap(requireActivity().contentResolver, it)
                    binding.imgProdutoDialog.setImageBitmap(bitmap)
                    imagemBase64 = ImageUtils.bitmapToBase64(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
