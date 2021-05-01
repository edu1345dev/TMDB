package com.example.movies.model

import com.google.gson.annotations.SerializedName

data class UpcomingMoviesPage(
    val dates: Dates? = Dates(),
    val page: Int? = 0,
    @SerializedName("results")
    val movies: List<Movie> = listOf(),
    @SerializedName("total_pages")
    val totalPages: Int? = 0,
    @SerializedName("total_results")
    val totalResults: Int? = 0
)