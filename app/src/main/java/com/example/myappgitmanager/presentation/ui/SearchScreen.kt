package com.example.myappgitmanager.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.myappgitmanager.R
import com.example.myappgitmanager.navigation.action.NavigationEvent
import com.example.myappgitmanager.navigation.destination.NavigationDestination
import com.example.myappgitmanager.presentation.ui.common.MultiTypeList
import com.example.myappgitmanager.presentation.ui.common.SearchAppBar
import com.example.myappgitmanager.presentation.ui.common.bounceClick
import com.example.myappgitmanager.presentation.ui.common.rememberAnimatedBrush
import com.example.myappgitmanager.presentation.viewmodel.SearchScreenVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@Composable
fun SearchScreenRoot(
    onNavigationEvent: (NavigationEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val MIN_QUERY_LENGTH = remember { 3 }

    val vm = koinViewModel<SearchScreenVM>()
    val uiState by vm.state.collectAsState()

    fun handleQuery(input: String) {
        if (input.length < MIN_QUERY_LENGTH) {
            vm.cancelQuery()
            vm.resetState()
            return
        }

        if (uiState.isLoading) {
            vm.cancelQuery()
        }

        vm.executeQuery(input)
    }

    var query by rememberSaveable { mutableStateOf("") }
    var lastCollectedQuery by rememberSaveable {  mutableStateOf("") }

    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(1000L)
            .collectLatest {query ->
                if (query != lastCollectedQuery || vm.state.value.searchScreenError != null) {
                    handleQuery(query)
                    lastCollectedQuery = query
                }
            }
    }

    fun onRepositoryClick(repositoryData: Map<String, Any?> = emptyMap()) {
        onNavigationEvent(
            NavigationEvent.NavigateTo(
                destination = NavigationDestination.Repository,
                args = repositoryData
            )
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            SearchAppBar(
                onSearchQueryEntered = {
                    query = it
                },
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                SearchScreenDataLoadingState(paddingValues = innerPadding)
            }
            uiState.searchScreenError != null -> {
                ScreenErrorState(
                    onRefreshStateClick = {
                        if (lastCollectedQuery.length >= MIN_QUERY_LENGTH) {
                            vm.executeQuery(query = lastCollectedQuery)
                        }
                    },
                    paddingValues = innerPadding
                )
            }
            uiState.data != null -> {
                uiState.data?.let {
                    if (it.isEmpty()) {
                        SearchScreenEmptyResultState(paddingValues = innerPadding)
                    } else {
                        MultiTypeList(
                            paddingValues = innerPadding,
                            data = it,
                            onRepositoryClick = ::onRepositoryClick
                        )
                    }
                }
            }
            else -> {
                SearchScreenStartState(paddingValues = innerPadding)
            }
        }
    }
}

@Composable
fun SearchScreenDataLoadingState(paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(6) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        brush = rememberAnimatedBrush(),
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

@Composable
fun SearchScreenStartState(
    paddingValues: PaddingValues
) {
    SearchScreenCommon(
        imageRes = R.drawable.search_screen_start,
        contentDescription = "Search screen start state screen",
        title = "Поиск",
        subtitle = "Начните вводить поисковый запрос (от 3-х символов), чтобы увидеть его результаты 😜",
        paddingValues = paddingValues
    )
}

@Composable
fun SearchScreenEmptyResultState(
    paddingValues: PaddingValues
) {
    SearchScreenCommon(
        imageRes = R.drawable.empty_search_result,
        contentDescription = "Search screen empty result state screen",
        title = "Тут ничего нет",
        subtitle = "Возможно чуть позже здесь что-то появится",
        paddingValues = paddingValues
    )
}

@Composable
fun ScreenErrorState(
    onRefreshStateClick: () -> Unit,
    paddingValues: PaddingValues
) {
    Column  {
        SearchScreenCommon(
            imageRes = R.drawable.search_error,
            contentDescription = "Search screen start state screen",
            title = "Упс...",
            subtitle = "Либо у вас слабое интернет-подключение, либо что-то сломалось у нас - тогда мы в курсе и уже работаем над этим",
            paddingValues = paddingValues
        )
        Button(
            onClick = { onRefreshStateClick() },
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .height(48.dp)
                .bounceClick(),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Text("Обновить")
        }
    }
}

@Composable
fun SearchScreenCommon(
    modifier: Modifier = Modifier,
    imageRes: Int,
    contentDescription: String,
    title: String,
    subtitle: String,
    paddingValues: PaddingValues,
) {
    ConstraintLayout(
        modifier = modifier
            .padding(paddingValues)
            .padding(32.dp)
    ) {
        val (image, titleRef, subtitleRef) = createRefs()

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    verticalBias = 0.15f
                }
        )

        Text(
            text = title,
            fontWeight = MaterialTheme.typography.headlineSmall.fontWeight,
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(image.bottom, margin = 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                verticalBias = 0.03f
            }
        )

        Text(
            text = subtitle,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(subtitleRef) {
                top.linkTo(titleRef.bottom, margin = 18.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                verticalBias = 0.03f
            }
        )
    }
}


