package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import kotlinx.coroutines.launch

@Composable
fun <T : Any> GenericListContentUi(
  listUiState: ListUiState<List<T>>,
  searchQuery: String,
  currentSortOptions: SortOptions,
  currentFilters: FilterOptions,
  viewType: String,
  onItemClick: (T) -> Unit,
  onFavoritesClick: () -> Unit,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onSettingsClick: () -> Unit,
  listState: LazyListState,
  gridState: LazyStaggeredGridState,
  getItemId: (T) -> Any,
  getItemTitle: (T) -> String,
  getItemOverview: (T) -> String,
  getItemPosterPath: (T) -> String?,
  getItemVoteAverage: (T) -> Float,
  isItemFavorite: (T) -> Boolean,
  toggleFavorite: (T) -> Unit,
  isLastPage: Boolean,
  loadMoreItems: () -> Unit,
  refreshItems: () -> Unit,
  setLastViewedItemIndex: (Int) -> Unit,
  setSearchQuery: (String) -> Unit,
  setSortOption: (SortOptions) -> Unit,
  setFilterOptions: (FilterOptions) -> Unit,
) {
  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var showFilterBottomSheet by rememberSaveable { mutableStateOf(false) }
  var expandedDropdown by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  var isRefreshing by remember { mutableStateOf(false) }

  val pullRefreshState =
    rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = {
        coroutineScope.launch {
          isRefreshing = true
          refreshItems()
          isRefreshing = false
        }
      },
    )

  if (showFilterBottomSheet) {
    FilterBottomSheet(
      currentFilters = currentFilters,
      onDismiss = { showFilterBottomSheet = false },
      onApply = { newFilters ->
        setFilterOptions(newFilters)
      },
    )
  }

  Scaffold(
    topBar = {
      TopBarUi(
        isSearchActive = isSearchActive,
        searchQuery = searchQuery,
        onSearchQueryChange = { setSearchQuery(it) },
        onSearchIconClick = { isSearchActive = true },
        onCloseSearchClick = {
          isSearchActive = false
          setSearchQuery("")
        },
        expandedDropdown = expandedDropdown,
        onSortOptionClick = {
          setSortOption(it)
          expandedDropdown = false
        },
        currentSortOptions = currentSortOptions,
        onDropdownExpand = { expandedDropdown = !expandedDropdown },
        onFavoritesClick = onFavoritesClick,
        onViewTypeChange = onViewTypeChange,
        viewType = viewType,
        onThemeChange = onThemeChange,
        currentThemeMode = currentThemeMode,
        onFilterClick = { showFilterBottomSheet = true },
        onSettingsClick = onSettingsClick,
      )
    },
  ) { paddingValues ->
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .pullRefresh(pullRefreshState),
    ) {
      when (listUiState) {
        is ListUiState.Loading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
          }
        }

        is ListUiState.Success -> {
          val items = listUiState.data
          when (viewType) {
            Constants.VIEW_TYPE_GRID ->
              ItemGridListUi(
                items = items,
                onItemClick = onItemClick,
                gridState = gridState,
                isLastPage = isLastPage,
                loadMoreItems = loadMoreItems,
                setLastViewedItemIndex = setLastViewedItemIndex,
                toggleFavorite = toggleFavorite,
                getItemId = getItemId,
                getItemTitle = getItemTitle,
                getItemPosterPath = getItemPosterPath,
                getItemVoteAverage = getItemVoteAverage,
                isItemFavorite = isItemFavorite,
              )
            Constants.VIEW_TYPE_LIST ->
              ItemSimpleListUi(
                items = items,
                onItemClick = onItemClick,
                listState = listState,
                isLastPage = isLastPage,
                loadMoreItems = loadMoreItems,
                setLastViewedItemIndex = setLastViewedItemIndex,
                toggleFavorite = toggleFavorite,
                getItemId = getItemId,
                getItemTitle = getItemTitle,
                getItemOverview = getItemOverview,
                getItemPosterPath = getItemPosterPath,
                getItemVoteAverage = getItemVoteAverage,
                isItemFavorite = isItemFavorite,
              )
          }
        }

        is ListUiState.Error -> {
          ErrorContentUi(
            error = listUiState.error,
            onRetry = { loadMoreItems() },
            onSettingsClick = onSettingsClick,
          )
        }
      }
      PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
      )
      Box(modifier = Modifier.matchParentSize()) {
        ShimmeringOverlayUi(
          isVisible = isRefreshing || listUiState is ListUiState.Loading,
        )
      }
    }
  }
}
