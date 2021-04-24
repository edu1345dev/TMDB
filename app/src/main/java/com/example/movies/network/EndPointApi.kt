package com.example.movies.network

import com.example.movies.modelMovies.MovieConfiguration
import com.example.movies.modelMovies.UpcomingMoviesPage
import retrofit2.http.GET
import retrofit2.http.Query

interface EndPointApi {
    @GET("configuration")
    suspend fun getMoviesConfiguration() : MovieConfiguration

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int) : UpcomingMoviesPage

}