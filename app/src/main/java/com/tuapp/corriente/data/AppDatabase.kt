package com.tuapp.corriente.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tuapp.corriente.data.dao.*
import com.tuapp.corriente.data.entity.*

@Database(
    entities = [
        Circuito::class,
        BloqueServicio::class,
        Alarma::class,
        RegistroHistorico::class
    ],
    version = 3,           // ← era 2, ahora 3
    exportSchema = false
)
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun circuitoDao(): CircuitoDao
    abstract fun bloqueServicioDao(): BloqueServicioDao
    abstract fun alarmaDao(): AlarmaDao
    abstract fun registroHistoricoDao(): RegistroHistoricoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "corriente.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}