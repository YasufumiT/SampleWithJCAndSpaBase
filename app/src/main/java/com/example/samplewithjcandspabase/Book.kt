package com.example.samplewithjcandspabase

import kotlinx.serialization.Serializable

data class Books(
    val books: List<Book> = listOf()
){
    @Serializable
    data class Book(
        val id: Int,
        val name: String,
        val description: String? = null,
        val image: String? = null,
        val star: Int = 0
    )
}