package com.example.movies.model

class FavoriteMovie(){
    var name: List<String> = listOf()

    constructor(name: List<String>): this(){
        this.name = name
    }
}