package com.ctma.prestamolab.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Cliente global de Supabase para la aplicación.
 * Se inicializa con los plugins necesarios: Postgrest (Base de datos), 
 * Auth (Autenticación) y Storage (Archivos).
 */
val supabase = createSupabaseClient(
    supabaseUrl = "https://YOUR_PROJECT_URL.supabase.co",
    supabaseKey = "YOUR_ANON_KEY"
) {
    install(Postgrest)
    install(Auth)
    install(Storage)
}
