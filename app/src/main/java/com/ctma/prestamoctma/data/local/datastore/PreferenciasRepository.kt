package com.ctma.prestamoctma.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ctma.prestamoctma.model.EstadoSolicitud
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenciasRepository(private val context: Context) {

    private object PreferencesKeys {
        val FILTRO_ESTADO = stringPreferencesKey("filtro_estado")
    }

    val filtroEstado: Flow<EstadoSolicitud?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.FILTRO_ESTADO]?.let {
            try {
                EstadoSolicitud.valueOf(it)
            } catch (_: Exception) {
                null
            }
        }
    }

    suspend fun guardarFiltroEstado(estado: EstadoSolicitud?) {
        context.dataStore.edit { preferences ->
            if (estado == null) {
                preferences.remove(PreferencesKeys.FILTRO_ESTADO)
            } else {
                preferences[PreferencesKeys.FILTRO_ESTADO] = estado.name
            }
        }
    }
}
