package com.example.movies.repository

import com.example.movies.model.MovieConfiguration

object SingletonConfiguration {
    var config: MovieConfiguration? = null
    fun setConfiguration(configuration: MovieConfiguration){
        config = configuration
    }
}