package com.example.movies.repository

import com.example.movies.network.EndPointApi
import com.example.movies.network.RetrofitInit

class RepositoryMovies{
    private var url = "https://api.themoviedb.org/3/"

    private val service = RetrofitInit(url).create(EndPointApi::class)

    suspend fun getConfiguration() = service.getMoviesConfiguration()

    suspend fun getUpcomingMovies(page: Int = 1) = service.getUpcomingMovies(page)
}