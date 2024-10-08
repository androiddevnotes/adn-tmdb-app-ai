package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import coil.compose.AsyncImage

@Composable
fun AlphaDetailBackgroundImageUi(posterPath: String?) {
  AsyncImage(
    model = "https://image.tmdb.org/t/p/w500$posterPath",
    contentDescription = null,
    modifier = Modifier.fillMaxSize(),
    contentScale = ContentScale.Crop,
  )
}
