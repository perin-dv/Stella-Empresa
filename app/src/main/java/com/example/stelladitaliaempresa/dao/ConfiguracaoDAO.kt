package com.example.stelladitaliaempresa.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stelladitaliaempresa.Entity.ConfiguracaoEntity

@Dao
interface ConfiguracaoDAO {  // <- Alterado aqui para "DAO"
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(config: ConfiguracaoEntity)

    @Query("SELECT * FROM configuracao WHERE idUsuario = :id LIMIT 1")
    suspend fun buscarPorId(id: String): ConfiguracaoEntity?
}
