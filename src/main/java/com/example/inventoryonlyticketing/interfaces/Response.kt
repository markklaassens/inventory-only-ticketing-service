package com.example.inventoryonlyticketing.interfaces

interface Response {
    fun getStatusCode(): Int
    fun getBody(): Any
}