package com.ctma.prestamoctma.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.storage.Storage

// TODO: Rellenar con valores reales de Supabase
private const val SUPABASE_URL = "https://gsuujbgcvkiorxpwntto.supabase.co"
private const val SUPABASE_KEY = "sb_publishable_fXAbrHB6-nuGzgYJZ5HDcg_30cTt0l5"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Postgrest)
    install(Auth)
    install(Storage)
}
