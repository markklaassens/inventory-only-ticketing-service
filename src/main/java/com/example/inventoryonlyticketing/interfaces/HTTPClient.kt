package com.example.inventoryonlyticketing.interfaces

interface HTTPClient {
    fun post(url: String, body: Any): Response
    fun get(url: String): Response
}