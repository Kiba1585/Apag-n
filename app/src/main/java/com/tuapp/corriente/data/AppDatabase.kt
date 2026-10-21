package com.tuapp.corriente.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tuapp.corriente.data.dao.AlarmaDao
import com.tuapp.corriente.data.dao.BloqueHorarioDao
import com.tuapp.corriente.data.dao.RegistroHistoricoDao
import com.tuapp.corriente.data.dao.ZonaDao
import com.tuapp.corriente.data.entity.Alarma
import com.tuapp.corriente.data.entity.BloqueHorario
import com.tuapp.corriente.data.entity.RegistroHistorico
import com.tuapp.corriente.data.entity.Zona

@Database(
    entities = [
        Zona::class,
        BloqueHorario::class,
        Alarma::class,
        RegistroHistorico::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun zonaDao(): ZonaDao
    abstract fun bloqueHorarioDao(): BloqueHorarioDao
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
                ).build().also { INSTANCE = it }
            }
        }
    }
}
