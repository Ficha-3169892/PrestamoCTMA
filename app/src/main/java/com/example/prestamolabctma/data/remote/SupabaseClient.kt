package com.example.prestamolabctma.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Cliente global de Supabase para la aplicación.
 */
val supabase = createSupabaseClient(
    supabaseUrl = "https://gsuujbgcvkiorxpwntto.supabase.co",
    supabaseKey = "sb_publishable_fXAbrHB6-nuGzgYJZ5HDcg_30cTt0l5"
) {
    install(Postgrest)
    install(Auth)
    install(Storage)
}
