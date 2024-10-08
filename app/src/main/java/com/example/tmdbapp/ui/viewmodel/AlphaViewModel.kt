package com.example.tmdbapp.ui.viewmodel

import android.app.*
import androidx.lifecycle.*
import com.example.tmdbapp.data.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.ui.viewmodel.handlers.BetaResultHandler
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.utils.ApiKeyManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AlphaViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private var searchJob: Job? = null

  private val _betaPieceUiState =
    MutableStateFlow<BetaPieceUiState<String>>(BetaPieceUiState.Idle)

  internal val _alphaDetailUiState =
    MutableStateFlow<AlphaDetailUiState<Movie>>(AlphaDetailUiState.Loading)

  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  val favorites: StateFlow<List<Movie>> = _favorites.asStateFlow()
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _scrollToIndex = MutableStateFlow<Int?>(null)
  private val _searchQuery = MutableStateFlow("")
  internal var currentPage = 1
  internal var isLastPage = false
  internal var isLoading = false
  internal val _alphaAuthUiState = MutableStateFlow<AlphaAuthUiState<String>>(AlphaAuthUiState.Idle)

  internal val _alphaCreateListUiState =
    MutableStateFlow<AlphaCreateListUiState<Int>>(AlphaCreateListUiState.Idle)

  internal val _alphaListUiState =
    MutableStateFlow<AlphaListUiState<List<Movie>>>(AlphaListUiState.Loading)

  internal val _currentSortOptions = MutableStateFlow(SortOptions.POPULAR)
  internal val _filterOptions = MutableStateFlow(FilterOptions())
  internal val apiKeyManager = ApiKeyManager(application)
  internal val repository = Repository(application)
  internal val sessionManagerPreferencesDataStore = SessionManagerPreferencesDataStore(application)
  val betaPieceUiState: StateFlow<BetaPieceUiState<String>> = _betaPieceUiState.asStateFlow()
  val currentSortOptions: StateFlow<SortOptions> = _currentSortOptions
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val alphaAuthUiState: StateFlow<AlphaAuthUiState<String>> = _alphaAuthUiState
  val alphaCreateListUiState: StateFlow<AlphaCreateListUiState<Int>> = _alphaCreateListUiState
  val alphaDetailUiState: StateFlow<AlphaDetailUiState<Movie>> = _alphaDetailUiState.asStateFlow()
  val alphaListUiState: StateFlow<AlphaListUiState<List<Movie>>> = _alphaListUiState.asStateFlow()
  val searchQuery: StateFlow<String> = _searchQuery

  init {
    fetchMovies() // This will fetch popular movies by default
    loadFavorites()
    checkAuthenticationStatus()
  }

  fun askAIAboutItem(movie: Movie) {
    viewModelScope.launch {
      _betaPieceUiState.value = BetaPieceUiState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      val result = repository.askOpenAi(prompt)
      BetaResultHandler.handleBetaResult(
        result = result,
        betaPieceUiState = _betaPieceUiState,
        apiKeyManager = apiKeyManager,
      )
    }
  }

  fun clearAIResponse() {
    _betaPieceUiState.value = BetaPieceUiState.Idle
  }

  fun clearScrollToIndex() {
    _scrollToIndex.value = null
  }

  fun isFavorite(movieId: Int): Boolean = favorites.value.any { it.id == movieId }

  fun loadMoreItems() {
    fetchMovies()
  }

  fun refreshItems() {
    currentPage = 1
    isLastPage = false
    _alphaListUiState.value = AlphaListUiState.Loading
    fetchMovies()
  }

  fun retryFetchItemDetails() {
    val currentState = _alphaDetailUiState.value
    if (currentState is AlphaDetailUiState.Error) {
      fetchMovieDetails(currentState.itemId)
    }
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _alphaListUiState.value = AlphaListUiState.Loading
    fetchMovies()
  }

  fun setLastViewedItemIndex(index: Int) {
    _lastViewedItemIndex.value = index
  }

  fun setSearchQuery(query: String) {
    val oldQuery = _searchQuery.value
    _searchQuery.value = query
    searchJob?.cancel()
    if (query.isNotEmpty()) {
      searchJob =
        viewModelScope.launch {
          delay(Constants.DELAY_SEARCH)
          searchMovies(query)
        }
    } else if (oldQuery.isNotEmpty() && query.isEmpty()) {
      refreshItems()
    }
  }

  fun setSortOption(sortOptions: SortOptions) {
    if (_currentSortOptions.value != sortOptions) {
      _currentSortOptions.value = sortOptions
      currentPage = 1
      isLastPage = false
      _alphaListUiState.value = AlphaListUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _alphaDetailUiState.update { currentState ->
        if (currentState is AlphaDetailUiState.Success && currentState.data.id == updatedMovie.id) {
          AlphaDetailUiState.Success(updatedMovie)
        } else {
          currentState
        }
      }

      _alphaListUiState.update { currentState ->
        when (currentState) {
          is AlphaListUiState.Success -> {
            val updatedMovies =
              currentState.data.map {
                if (it.id == updatedMovie.id) updatedMovie else it
              }
            AlphaListUiState.Success(updatedMovies)
          }
          else -> currentState
        }
      }

      loadFavorites()
    }
  }

  fun loadFavorites() {
    viewModelScope.launch {
      repository.getFavoriteMovies().collectLatest { favoriteMovies ->
        _favorites.value = favoriteMovies
      }
    }
  }
}
