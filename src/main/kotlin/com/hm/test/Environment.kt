package com.hm.test

const val ENV_VAR_1 = "ENV_VAR_1"

object Environment {
    val ENV_VAR_1 = getEnvOrDefault("ENV_VAR_1", "")
    val ENV_VAR_2 = getEnvOrDefault("ENV_VAR_2", "default_value")
        
    fun getEnvOrDefault(name: String, default: String) = System.getenv(name) ?: default
}