package com.tuapp.corriente.data

import android.content.Context
import com.tuapp.corriente.data.entity.Circuito
import com.tuapp.corriente.data.entity.ReglaHorario
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime

class ScheduleManager(context: Context) {
    private val prefs = context.getSharedPreferences("corriente_prefs", Context.MODE_PRIVATE)

    fun guardarReglas(reglas: List<ReglaHorario>) {
        val jsonArray = JSONArray()
        for (r in reglas) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("circuitoCodigo", r.circuitoCodigo)
            obj.put("inicioAncla", r.inicioAncla.toString())
            obj.put("horasCorriente", r.horasCorriente)
            obj.put("horasApagon", r.horasApagon)
            jsonArray.put(obj)
        }
        prefs.edit().putString("reglas_json", jsonArray.toString()).apply()
    }

    fun cargarReglas(): List<ReglaHorario>? {
        val jsonStr = prefs.getString("reglas_json", null) ?: return null
        return try {
            val jsonArray = JSONArray(jsonStr)
            val lista = mutableListOf<ReglaHorario>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                lista.add(
                    ReglaHorario(
                        id = obj.getLong("id"),
                        circuitoCodigo = obj.getString("circuitoCodigo"),
                        inicioAncla = LocalDateTime.parse(obj.getString("inicioAncla")),
                        horasCorriente = obj.getLong("horasCorriente"),
                        horasApagon = obj.getLong("horasApagon")
                    )
                )
            }
            lista
        } catch (e: Exception) { null }
    }

    // ... (Las funciones de favoritos, circuitos y alarmas se mantienen igual)
    fun guardarCircuitoFavorito(codigo: String) { prefs.edit().putString("circuito_favorito", codigo).apply() }
    fun obtenerCircuitoFavorito(): String? = prefs.getString("circuito_favorito", null)
    
    fun guardarCircuitos(circuitos: List<Circuito>) {
        val jsonArray = JSONArray()
        for (c in circuitos) {
            val obj = JSONObject()
            obj.put("codigo", c.codigo)
            obj.put("nombre", c.nombre)
            obj.put("zona", c.zona)
            obj.put("descripcion", c.descripcion)
            obj.put("color", c.color)
            obj.put("activo", c.activo)
            obj.put("tieneSalud", c.tieneSalud)
            obj.put("tieneBombeo", c.tieneBombeo)
            obj.put("tieneEducacion", c.tieneEducacion)
            obj.put("tieneTelecom", c.tieneTelecom)
            obj.put("prioridadMaxima", c.prioridadMaxima)
            jsonArray.put(obj)
        }
        prefs.edit().putString("circuitos_json", jsonArray.toString()).apply()
    }

    fun cargarCircuitos(): List<Circuito>? {
        val jsonStr = prefs.getString("circuitos_json", null) ?: return null
        return try {
            val jsonArray = JSONArray(jsonStr)
            val lista = mutableListOf<Circuito>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                lista.add(
                    Circuito(
                        codigo = obj.getString("codigo"),
                        nombre = obj.getString("nombre"),
                        zona = obj.getString("zona"),
                        descripcion = obj.getString("descripcion"),
                        color = obj.getInt("color"),
                        activo = obj.optBoolean("activo", true),
                        tieneSalud = obj.optBoolean("tieneSalud", false),
                        tieneBombeo = obj.optBoolean("tieneBombeo", false),
                        tieneEducacion = obj.optBoolean("tieneEducacion", false),
                        tieneTelecom = obj.optBoolean("tieneTelecom", false),
                        prioridadMaxima = obj.optBoolean("prioridadMaxima", false)
                    )
                )
            }
            lista
        } catch (e: Exception) { null }
    }

    fun activarAlarma(codigo: String) {
        val actuales = obtenerAlarmasActivas().toMutableSet()
        actuales.add(codigo)
        prefs.edit().putStringSet("alarmas_activas", actuales).apply()
    }
    fun desactivarAlarma(codigo: String) {
        val actuales = obtenerAlarmasActivas().toMutableSet()
        actuales.remove(codigo)
        prefs.edit().putStringSet("alarmas_activas", actuales).apply()
    }
    fun obtenerAlarmasActivas(): Set<String> = prefs.getStringSet("alarmas_activas", emptySet()) ?: emptySet()

    fun onboardingCompletado(): Boolean = prefs.getBoolean("onboarding_completado", false)
    fun marcarOnboardingCompletado() { prefs.edit().putBoolean("onboarding_completado", true).apply() }
}