package com.example.movies.view

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.view.adapter.MoviesAdapter
import com.example.movies.view.adapter.RecyclerScrollListener
import com.example.movies.viewmodel.MainViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var adapter: MoviesAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }
    private val nextPageLoading by lazy { findViewById<ProgressBar>(R.id.nextLoading) }
    private val firstPageLoading by lazy { findViewById<ProgressBar>(R.id.firstLoading) }
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val recyclerScrollListener by lazy {
        RecyclerScrollListener {
            viewModel.requestNextPage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler_home)

        //config recycler
        adapter = MoviesAdapter()
        recycler.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager

        recycler.addOnScrollListener(recyclerScrollListener)
        firebaseAnalytics = Firebase.analytics

        viewModel.moviesList.observe(this) {movies ->
            enableRecyclerScrollPaging()
            adapter.addMovies(movies)
            firebaseAnalytics.setUserProperty("movies", "movie")

            val params = Bundle().apply {
                putString("movie_name", movies[0].title)
            }

            firebaseAnalytics.logEvent("share_image", params)
        }


        viewModel.firstPageLoading.observe(this) {
            if (it) {
                firstPageLoading.visibility = VISIBLE
            } else {
                firstPageLoading.visibility = GONE
            }
        }

        viewModel.nextPageLoading.observe(this) {
            if (it) {
                nextPageLoading.visibility = VISIBLE
            } else {
                nextPageLoading.visibility = GONE
            }
        }

        viewModel.errorMessage.observe(this, Observer {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun enableRecyclerScrollPaging() {
        recyclerScrollListener.requesting = false
    }
}
