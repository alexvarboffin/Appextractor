package com.walhalla.compose.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.compose.ui.unit.dp


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<ItemType?>(null) }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Обработка нажатия кнопки "назад"
    BackHandler(enabled = active) {
        if (active) {
            active = false
            query = ""
            focusManager.clearFocus()
        } else {
            onNavigateBack()
        }
    }

    // Имитация данных
    val allItems = remember {
        listOf(
            SearchItem(1, "Недавний документ", "Открыт вчера", ItemType.RECENT),
            SearchItem(2, "Важная заметка", "Помечена как избранное", ItemType.FAVORITE),
            SearchItem(3, "Проект Android", "Kotlin, Jetpack Compose", ItemType.REGULAR),
            SearchItem(4, "Список задач", "Приоритетные задачи", ItemType.REGULAR),
            SearchItem(5, "Избранная статья", "Material Design 3", ItemType.FAVORITE)
        )
    }

    // Фильтрация элементов
    val filteredItems = remember(query, selectedFilter) {
        allItems.filter { item ->
            val matchesQuery = item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)
            val matchesFilter = selectedFilter == null || item.type == selectedFilter
            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                query = query,
                onQueryChange = {
                    query = it
                    isLoading = true
                    scope.launch {
                        delay(300) // Имитация задержки поиска
                        isLoading = false
                    }
                },
                onSearch = {
                    active = false
                    focusManager.clearFocus()
                },
                active = active,
                onActiveChange = { isActive ->
                    active = isActive
                    if (!isActive) {
                        query = ""
                        selectedFilter = null
                        focusManager.clearFocus()
                    }
                },
                placeholder = { Text("Поиск") },
                leadingIcon = {
                    if (active) {
                        IconButton(
                            onClick = {
                                active = false
                                query = ""
                                selectedFilter = null
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(Icons.Default.ArrowBack, "Назад")
                        }
                    } else if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, "Очистить")
                        }
                    }
                }
            ) {
                // Фильтры
                AnimatedVisibility(
                    visible = filteredItems.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == null,
                            onClick = { selectedFilter = null },
                            label = { Text("Все") }
                        )
                        FilterChip(
                            selected = selectedFilter == ItemType.RECENT,
                            onClick = { selectedFilter = ItemType.RECENT },
                            label = { Text("Недавние") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                        FilterChip(
                            selected = selectedFilter == ItemType.FAVORITE,
                            onClick = { selectedFilter = ItemType.FAVORITE },
                            label = { Text("Избранное") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }

                // Результаты поиска
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ничего не найдено",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filteredItems,
                            key = { it.id }
                        ) { item ->
                            SearchResultItem1(
                                item = item,
                                modifier = Modifier.animateItemPlacement(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Основной список (когда поиск не активен)
        AnimatedVisibility(
            visible = !active,
            enter = fadeIn() + expandIn(expandFrom = Alignment.TopCenter),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.TopCenter)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = allItems,
                    key = { it.id }
                ) { item ->
                    SearchResultItem1(
                        item = item,
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultItem1(
    item: SearchItem,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = {
            Text(
                text = item.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = item.description,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = {
            Icon(
                imageVector = when (item.type) {
                    ItemType.RECENT -> Icons.Default.History
                    ItemType.FAVORITE -> Icons.Default.Star
                    ItemType.REGULAR -> Icons.Default.Article
                },
                contentDescription = null,
                tint = when (item.type) {
                    ItemType.RECENT -> MaterialTheme.colorScheme.secondary
                    ItemType.FAVORITE -> MaterialTheme.colorScheme.primary
                    ItemType.REGULAR -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}