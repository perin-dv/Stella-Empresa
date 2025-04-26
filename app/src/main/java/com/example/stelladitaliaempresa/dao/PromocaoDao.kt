package com.example.stelladitaliaempresa.dao

import androidx.room.*
import com.example.stelladitaliaempresa.Entity.PromocaoEntity

@Dao
interface PromocaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(promocao: PromocaoEntity)

    @Query("SELECT * FROM promocoes ORDER BY id DESC")
    suspend fun getAll(): List<PromocaoEntity>


    @Query("DELETE FROM promocoes WHERE id = :id")
    suspend fun deleteById(id: String)

}
