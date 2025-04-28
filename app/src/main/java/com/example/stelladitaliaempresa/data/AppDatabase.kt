package com.example.stelladitaliaempresa.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stelladitaliaempresa.Entity.ConfiguracaoEntity
import com.example.stelladitaliaempresa.Entity.ProdutoEntity
import com.example.stelladitaliaempresa.Entity.PromocaoEntity
import com.example.stelladitaliaempresa.dao.ConfiguracaoDAO
import com.example.stelladitaliaempresa.dao.ProdutoDao
import com.example.stelladitaliaempresa.dao.PromocaoDao
import com.example.stelladitaliaempresa.util.Converters


@Database(
    entities = [ProdutoEntity::class, PromocaoEntity::class, ConfiguracaoEntity::class],
    version = 8, // << aumente a versão!
    exportSchema = false
)


@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun produtoDao(): ProdutoDao
    abstract fun configuracaoDao(): ConfiguracaoDAO
    abstract fun promocaoDao(): PromocaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // <-- ESSA LINHA
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}
