package com.example.movies.network

import com.example.movies.model.MovieConfiguration
import com.example.movies.model.UpcomingMoviesPage
import retrofit2.http.GET
import retrofit2.http.Query

interface EndPointApi {
    @GET("configuration")
    suspend fun getMoviesConfiguration() : MovieConfiguration

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int) : UpcomingMoviesPage

}