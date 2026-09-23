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
    supabaseUrl = "https://rdgqqulqdwzmbebknewj.supabase.co",
    supabaseKey = "sb_publishable_lYR4qb9qgykRvm3cSSQQNA_8aQRyJHB"
) {
    install(Postgrest)
    install(Auth)
    install(Storage)
}
