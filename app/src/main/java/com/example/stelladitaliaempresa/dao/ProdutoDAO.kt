package com.example.stelladitaliaempresa.dao


import androidx.room.*
import com.example.stelladitaliaempresa.Entity.ProdutoEntity


@Dao
interface ProdutoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(produto: ProdutoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(produtos: List<ProdutoEntity>)

    @Update
    fun update(produto: ProdutoEntity)

    @Delete
    fun delete(produto: ProdutoEntity)

    @Query("DELETE FROM produtos")
    suspend fun deletarTodos()


    @Query("SELECT * FROM produtos")
    fun getTodos(): List<ProdutoEntity>
}

