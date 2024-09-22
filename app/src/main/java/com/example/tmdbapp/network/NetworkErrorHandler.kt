package com.example.tmdbapp.network

import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.AppError
import kotlinx.coroutines.flow.first
import timber.log.Timber

suspend fun handleNetworkError(
  errorMessage: String?,
  apiKeyManager: ApiKeyManager,
): AppError =
  when {
    apiKeyManager.tmdbApiKeyFlow.first().isBlank() -> {
      AppError.ApiKeyMissing
    }
    errorMessage?.contains("UnknownHostException") == true -> {
      AppError.NoInternet
    }
    errorMessage?.contains("IOException") == true -> {
      AppError.Network
    }
    errorMessage?.contains("ResponseException") == true -> {
      if (errorMessage.contains("5")) {
        AppError.Server
      } else if (errorMessage.contains("401") || errorMessage.contains("Invalid API key")) {
        AppError.ApiKeyMissing
      } else {
        Timber.e("Unknown error ... : $errorMessage")
        AppError.ApiError(errorMessage)
      }
    }

    else -> {
      Timber.e("Unknown error: $errorMessage")
      AppError.Unknown
    }
  }
