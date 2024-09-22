package com.example.tmdbapp.models

import com.example.tmdbapp.utils.AppError

sealed class AlphaListUiState<out T> {
  data class Error(
    val error: AppError,
  ) : AlphaListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AlphaListUiState<T>()

  data object Loading : AlphaListUiState<Nothing>()
}

sealed class AlphaDetailUiState<out T> {
  data object Loading : AlphaDetailUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AlphaDetailUiState<T>()

  data class Error(
    val error: AppError,
    val itemId: Int,
  ) : AlphaDetailUiState<Nothing>()
}

sealed class BetaPieceUiState<out T> {
  data object Idle : BetaPieceUiState<Nothing>()

  data object Loading : BetaPieceUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : BetaPieceUiState<T>()

  data class Error(
    val error: AppError,
  ) : BetaPieceUiState<Nothing>()
}

sealed class AlphaAuthUiState<out T> {
  data object Idle : AlphaAuthUiState<Nothing>()

  data object Loading : AlphaAuthUiState<Nothing>()

  data class RequestTokenCreated<T>(
    val data: T,
  ) : AlphaAuthUiState<T>()

  data object Authenticated : AlphaAuthUiState<Nothing>()

  data class Error(
    val message: String,
  ) : AlphaAuthUiState<Nothing>()
}

sealed class AlphaCreateListUiState<out T> {
  data object Idle : AlphaCreateListUiState<Nothing>()

  data object Loading : AlphaCreateListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AlphaCreateListUiState<T>()

  data class Error(
    val message: String,
  ) : AlphaCreateListUiState<Nothing>()
}
