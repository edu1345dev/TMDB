package com.example.movies.modelMovies

data class MovieConfiguration(
    val change_keys: List<String>? = listOf(),
    val images: Images? = Images()
)