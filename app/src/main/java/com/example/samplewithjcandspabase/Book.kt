package com.example.samplewithjcandspabase

import kotlinx.serialization.Serializable

data class Books(
    val books: List<Book> = listOf()
){
    @Serializable
    data class Book(
        val id: Int,
        val name: String,
        val discription: String? = null,
        val image: String? = null
    )
}