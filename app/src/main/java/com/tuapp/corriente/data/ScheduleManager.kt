package com.tuapp.corriente.data

import android.content.Context
import com.tuapp.corriente.data.entity.BloqueServicio
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalTime

class ScheduleManager(context: Context) {
    private val prefs = context.getSharedPreferences("corriente_prefs", Context.MODE_PRIVATE)

    fun guardarBloques(bloques: List<BloqueServicio>) {
        val jsonArray = JSONArray()
        for (b in bloques) {
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("circuitoCodigo", b.circuitoCodigo)
            obj.put("horaInicio", b.horaInicio.toString())
            obj.put("horaFin", b.horaFin.toString())
            jsonArray.put(obj)
        }
        prefs.edit().putString("bloques_json", jsonArray.toString()).apply()
    }

    fun cargarBloques(): List<BloqueServicio>? {
        val jsonStr = prefs.getString("bloques_json", null) ?: return null
        return try {
            val jsonArray = JSONArray(jsonStr)
            val lista = mutableListOf<BloqueServicio>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                lista.add(
                    BloqueServicio(
                        id = obj.getLong("id"),
                        circuitoCodigo = obj.getString("circuitoCodigo"),
                        horaInicio = LocalTime.parse(obj.getString("horaInicio")),
                        horaFin = LocalTime.parse(obj.getString("horaFin"))
                    )
                )
            }
            lista
        } catch (e: Exception) {
            null
        }
    }

    // NUEVAS FUNCIONES PARA EL WIDGET
    fun guardarCircuitoFavorito(codigo: String) {
        prefs.edit().putString("circuito_favorito", codigo).apply()
    }

    fun obtenerCircuitoFavorito(): String? {
        return prefs.getString("circuito_favorito", null)
    }
}