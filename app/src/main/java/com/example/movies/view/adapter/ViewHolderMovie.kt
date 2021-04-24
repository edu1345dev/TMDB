package com.example.movies.view.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.movies.R

class ViewHolderMovie(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val image: ImageView = itemView.findViewById(R.id.imgcharacter_card)
    val name: TextView = itemView.findViewById(R.id.txt_card)

}