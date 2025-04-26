// HomeActivity.kt - corrigido
package com.example.stelladitaliaempresa.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.stelladitaliaempresa.R
import com.example.stelladitaliaempresa.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var autenticacao: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        autenticacao = FirebaseAuth.getInstance()



        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = binding.bottomNavView

        verificarPedidosPendentes()

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_Pedidos -> {
                    navController.navigate(R.id.navigation_Pedidos)
                    true
                }
                R.id.navigation_configuracao -> {
                    navController.navigate(R.id.navigation_configuracao)
                    true
                }
                else -> false
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_Pedidos, R.id.navigation_configuracao
            )
        )

        binding.bottomNavView.setupWithNavController(navController)

        val badge = binding.bottomNavView.getOrCreateBadge(R.id.navigation_Pedidos)
        badge.isVisible = true // ou false quando não houver novos pedidos
        badge.number = 3 // número de pedidos novos, por exemplo


        // Evita reiniciar o fragmento atual
        binding.bottomNavView.setOnItemSelectedListener { item ->
            if (item.itemId != navController.currentDestination?.id) {
                navController.navigate(item.itemId)
            }
            true
        }

        // FAB Novo Produto
        binding.fabNovoProduto.setOnClickListener {
            startActivity(Intent(this, NovoProdutoActivity::class.java))
        }
    }

    private fun verificarPedidosPendentes() {
        val idEmpresa = "7a3118oNdgcpmwSqrgyRTqBnFFx2"
        val pedidosRef = FirebaseDatabase.getInstance()
            .getReference("pedidos")
            .child(idEmpresa)

        pedidosRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (pedidoSnapshot in snapshot.children) {
                    val status = pedidoSnapshot.child("status").getValue(String::class.java)
                    if (status == "pendente") count++
                }
                atualizarBadge(count)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun atualizarBadge(count: Int) {
        val badge = binding.bottomNavView.getOrCreateBadge(R.id.navigation_Pedidos)
        if (count > 0) {
            badge.isVisible = true
            badge.number = count
        } else {
            badge.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_empresa, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.novo_produtos -> {
                startActivity(Intent(this, HistoricoPromocoesActivity::class.java))
                true
            }
            R.id.text_sair -> {
                autenticacao.signOut()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
