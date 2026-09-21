package com.ctma.prestamoctma.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.Auth

// TODO: Rellenar con valores reales de Supabase
private const val SUPABASE_URL = "https://your-project.supabase.co"
private const val SUPABASE_KEY = "your-anon-key"

val supabase = createSupabaseClient(
    supabaseUrl = SUPABASE_URL,
    supabaseKey = SUPABASE_KEY
) {
    install(Postgrest)
    install(Auth)
}
