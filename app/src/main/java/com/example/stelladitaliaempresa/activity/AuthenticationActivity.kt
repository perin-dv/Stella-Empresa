package com.example.apkstelladitalia20.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.stelladitaliaempresa.R
import com.example.stelladitaliaempresa.activity.HomeActivity
import com.example.stelladitaliaempresa.base.BaseActivity
import com.example.stelladitaliaempresa.databinding.ActivityAuthenticationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser


class AuthenticationActivity : BaseActivity() {
    private lateinit var botaoAcessar: Button
    private lateinit var campoEmail: EditText
    private lateinit var campoSenha: EditText
    private lateinit var autenticacao: FirebaseAuth
    private lateinit var binding: ActivityAuthenticationBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var tipoAcesso: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        inicializarComponentes()
        autenticacao = FirebaseAuth.getInstance()
        verificarUsuarioLogado()
    }


    fun inicializarComponentes() {
        botaoAcessar = findViewById(R.id.button_acesso)
        campoEmail = findViewById(R.id.edittext_email)
        tipoAcesso = findViewById(R.id.switch_login)
        campoSenha = findViewById(R.id.editText_Password)

        botaoAcessar.setOnClickListener {
            val email = campoEmail.text.toString()
            val senha = campoSenha.text.toString()
            val tipo = tipoAcesso.isChecked

            when {
                email.isNotEmpty() && senha.isNotEmpty() -> {
                    if (tipo) {
                        // Acessar como cadastro (criação de conta)
                        autenticacao.createUserWithEmailAndPassword(email, senha)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val usuario = autenticacao.currentUser
                                    Toast.makeText(
                                        this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    abrirHome()
                                } else {
                                    erroExecao(task.exception)
                                }

                            }
                    } else {
                        // Acessar como login (sign in)
                        autenticacao.signInWithEmailAndPassword(email, senha)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Login realizado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    abrirHome()
                                } else {
                                    erroExecao(task.exception)
                                }
                            }
                    }
                }

                else -> {
                    if (email.isEmpty()) campoEmail.error = "Preencha o email"
                    if (senha.isEmpty()) campoSenha.error = "Preencha a senha"
                }
            }
        }
    }


    private fun erroExecao(exception: Exception?) {
        val erroExcecao: String = try {
            throw exception ?: Exception("Erro desconhecido")
        } catch (e: FirebaseAuthWeakPasswordException) {
            "Digite uma senha mais forte!"
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            "Por favor, digite um e-mail válido!"
        } catch (e: FirebaseAuthUserCollisionException) {
            "Esta conta já foi cadastrada!"
        } catch (e: Exception) {
            "Erro ao cadastrar usuário: ${e.message}"
        }

        Toast.makeText(this, erroExcecao, Toast.LENGTH_SHORT).show()
    }

    private fun abrirHome() {
        val i = Intent(this, HomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual: FirebaseUser? = autenticacao.currentUser
        if (usuarioAtual != null) {

            Toast.makeText(this, "Usuário já está logado!", Toast.LENGTH_SHORT).show()
            abrirHome()
        }
    }
}





