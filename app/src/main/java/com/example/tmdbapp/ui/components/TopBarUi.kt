package com.example.tmdbapp.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.*
import com.example.tmdbapp.R
import com.example.tmdbapp.models.SortOptions
import com.example.tmdbapp.ui.theme.*
import com.example.tmdbapp.utils.*

@Composable
fun TopBarUi(
  isSearchActive: Boolean,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  onSearchIconClick: () -> Unit,
  onCloseSearchClick: () -> Unit,
  expandedDropdown: Boolean,
  onSortOptionClick: (SortOptions) -> Unit,
  currentSortOptions: SortOptions,
  onDropdownExpand: () -> Unit,
  onFavoritesClick: () -> Unit,
  onViewTypeChange: (String) -> Unit,
  viewType: String,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onFilterClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  if (isSearchActive) {
    SearchTopBar(
      searchQuery = searchQuery,
      onSearchQueryChange = onSearchQueryChange,
      onCloseSearchClick = onCloseSearchClick,
    )
  } else {
    MainTopBar(
      expandedDropdown = expandedDropdown,
      onSortOptionClick = onSortOptionClick,
      currentSortOptions = currentSortOptions,
      onDropdownExpand = onDropdownExpand,
      onSearchIconClick = onSearchIconClick,
      onFavoritesClick = onFavoritesClick,
      onViewTypeChange = onViewTypeChange,
      viewType = viewType,
      onThemeChange = onThemeChange,
      currentThemeMode = currentThemeMode,
      onFilterClick = onFilterClick,
      onSettingsClick = onSettingsClick,
    )
  }
}

@Composable
private fun MainTopBar(
  expandedDropdown: Boolean,
  onSortOptionClick: (SortOptions) -> Unit,
  currentSortOptions: SortOptions,
  onDropdownExpand: () -> Unit,
  onSearchIconClick: () -> Unit,
  onFavoritesClick: () -> Unit,
  onViewTypeChange: (String) -> Unit,
  viewType: String,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onFilterClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  TopAppBar(
    title = { AppLogo() },
    actions = {
      SearchIcon(onClick = onSearchIconClick)
      SortDropdown(
        expanded = expandedDropdown,
        onSortOptionClick = onSortOptionClick,
        currentSortOptions = currentSortOptions,
        onDropdownExpand = onDropdownExpand,
      )
      FavoritesIcon(onClick = onFavoritesClick)
      ViewTypeIcon(
        viewType = viewType,
        onViewTypeChange = onViewTypeChange,
      )
      ThemeIcon(
        currentThemeMode = currentThemeMode,
        onThemeChange = onThemeChange,
      )
      FilterIcon(onClick = onFilterClick)
      IconButton(onClick = onSettingsClick) {
        Image(
          painter = painterResource(id = R.drawable.cool_shape_settings),
          contentDescription = stringResource(R.string.settings),
          contentScale = ContentScale.Fit,
          modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
        )
      }
    },
    colors = topAppBarColors(),
  )
}

@Composable
private fun AppLogo() {
  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center,
  ) {
    Image(
      painter = painterResource(id = R.drawable.cool_shape_arrow_right),
      contentDescription = stringResource(R.string.app_name),
      modifier =
        Modifier
          .size(Constants.ICON_SIZE_MEDIUM)
          .align(Alignment.Center),
    )
  }
}

@Composable
private fun SearchIcon(onClick: () -> Unit) {
  IconButton(onClick = onClick) {
    Image(
      painter = painterResource(id = R.drawable.cool_shape_search),
      contentDescription = stringResource(R.string.content_desc_search),
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
    )
  }
}

@Composable
private fun SortDropdown(
  expanded: Boolean,
  onSortOptionClick: (SortOptions) -> Unit,
  currentSortOptions: SortOptions,
  onDropdownExpand: () -> Unit,
) {
  Box {
    IconButton(onClick = onDropdownExpand) {
      Image(
        painter = painterResource(id = R.drawable.cool_shape_sort),
        contentDescription = stringResource(R.string.content_desc_sort),
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
      )
    }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = onDropdownExpand,
      modifier = Modifier.background(MaterialTheme.colorScheme.surface),
    ) {
      SortOptions.entries.forEach { sortOption ->
        DropdownMenuItem(
          text = {
            Text(
              text = stringResource(sortOption.stringRes),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          },
          onClick = { onSortOptionClick(sortOption) },
          leadingIcon = {
            if (sortOption == currentSortOptions) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
              )
            }
          },
          modifier =
            Modifier.padding(
              horizontal = Constants.PADDING_MEDIUM,
              vertical = Constants.PADDING_SMALL,
            ),
        )
      }
    }
  }
}

@Composable
private fun FavoritesIcon(onClick: () -> Unit) {
  IconButton(onClick = onClick) {
    Image(
      painter = painterResource(id = R.drawable.cool_shape_fav),
      contentDescription = stringResource(R.string.content_desc_favorites),
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
    )
  }
}

@Composable
private fun ViewTypeIcon(
  viewType: String,
  onViewTypeChange: (String) -> Unit,
) {
  IconButton(onClick = {
    onViewTypeChange(
      if (viewType == Constants.VIEW_TYPE_GRID) {
        Constants.VIEW_TYPE_LIST
      } else {
        Constants.VIEW_TYPE_GRID
      },
    )
  }) {
    Image(
      painter =
        painterResource(
          id =
            if (viewType == Constants.VIEW_TYPE_GRID) {
              R.drawable.cool_shape_list
            } else {
              R.drawable.cool_shape_grid
            },
        ),
      contentDescription = stringResource(R.string.content_desc_switch_view),
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
    )
  }
}

@Composable
private fun ThemeIcon(
  currentThemeMode: ThemeMode,
  onThemeChange: () -> Unit,
) {
  IconButton(onClick = onThemeChange) {
    Image(
      painter =
        painterResource(
          id =
            when (currentThemeMode) {
              ThemeMode.LIGHT -> R.drawable.cool_shape_night
              ThemeMode.DARK -> R.drawable.cool_shape_light
              ThemeMode.SYSTEM -> R.drawable.cool_shape_theme_system
            },
        ),
      contentDescription = stringResource(R.string.content_desc_toggle_theme),
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
    )
  }
}

@Composable
private fun FilterIcon(onClick: () -> Unit) {
  IconButton(onClick = onClick) {
    Image(
      painter = painterResource(id = R.drawable.cool_shape_filter),
      contentDescription = stringResource(R.string.content_desc_filter),
      contentScale = ContentScale.Fit,
      modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
    )
  }
}

@Composable
private fun topAppBarColors() =
  TopAppBarDefaults.topAppBarColors(
    containerColor = MaterialTheme.colorScheme.surface,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
  )

@Composable
private fun SearchTopBar(
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  onCloseSearchClick: () -> Unit,
) {
  val focusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current

  LaunchedEffect(Unit) {
    focusRequester.requestFocus()
  }

  TextField(
    value = searchQuery,
    onValueChange = onSearchQueryChange,
    modifier =
      Modifier
        .fillMaxWidth()
        .focusRequester(focusRequester),
    placeholder = {
      Text(stringResource(R.string.label_search_movies))
    },
    leadingIcon = {
      Icon(
        imageVector = Icons.Default.Search,
        contentDescription = stringResource(R.string.content_desc_search),
      )
    },
    trailingIcon = {
      IconButton(onClick = {
        onCloseSearchClick()
        focusManager.clearFocus()
      }) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = stringResource(R.string.content_desc_close_search),
        )
      }
    },
    singleLine = true,
    colors =
      TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
      ),
  )
}
