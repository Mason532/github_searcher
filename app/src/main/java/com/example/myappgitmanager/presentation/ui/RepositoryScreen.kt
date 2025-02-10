package com.example.myappgitmanager.presentation.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myappgitmanager.R
import com.example.myappgitmanager.navigation.action.NavigationEvent
import com.example.myappgitmanager.navigation.args.NavArgs
import com.example.myappgitmanager.navigation.destination.NavigationDestination
import com.example.myappgitmanager.presentation.models.Content
import com.example.myappgitmanager.presentation.models.ContentType
import com.example.myappgitmanager.presentation.ui.common.NavTopBar
import com.example.myappgitmanager.presentation.ui.common.rememberAnimatedBrush
import com.example.myappgitmanager.presentation.viewmodel.RepositoryScreenVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import org.koin.androidx.compose.koinViewModel

@Composable
fun RepositoryScreenRoot(
    onNavigationEvent: (NavigationEvent) -> Unit,
    ownerId: String,
    repositoryId: String,
    modifier: Modifier = Modifier,
    path: String?
) {
    val vm = koinViewModel<RepositoryScreenVM>()
    val uiState by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.getRepositoryContent(owner = ownerId, repo = repositoryId, path = path ?: "")
    }

    fun onDirClick(repositoryData: Map<String, Any?> = emptyMap()) {
        val updatedArgs = repositoryData + mapOf(
            NavArgs.OWNER_ID to ownerId,
            NavArgs.REPOSITORY_ID to repositoryId,
        )

        onNavigationEvent(
            NavigationEvent.NavigateTo(
                destination = NavigationDestination.Repository,
                args = updatedArgs
            )
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            NavTopBar(
                title = path?.substringAfterLast("/") ?: repositoryId,
                canNavigateBack = true,
                navigateUp = {onNavigationEvent(NavigationEvent.NavigateBack)}
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                RepositoryScreenLoadingState(paddingValues = innerPadding)
            }
            uiState.repositoryScreenError != null -> {
                ScreenErrorState(
                    paddingValues = innerPadding,
                    onRefreshStateClick = { vm.getRepositoryContent(ownerId, repositoryId, path ?: "") }
                )
            }
            uiState.data != null -> {
                uiState.data?.let {
                    RepositoryScreenDataListState(
                        onDirClick = ::onDirClick,
                        data = it,
                        firstVisibleItemIndex = vm.scrollPosition,
                        firstVisibleItemScrollOffset = vm.scrollOffset,
                        onListScrollPositionChanged = vm::updateScrollPosition,
                        paddingValues = innerPadding,
                    )
                }
            }
        }
    }

}

@Composable
fun RepositoryScreenLoadingState(paddingValues: PaddingValues,) {
    val brush = rememberAnimatedBrush()

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(4.dp)
    ) {
        items(10) { index ->
            Column {
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(
                        modifier = Modifier.size(30.dp)
                            .clip(CircleShape)
                            .background(brush)
                    )
                    Spacer(
                        modifier = Modifier
                            .height(35.dp)
                            .fillMaxWidth(0.97f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(brush)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush)
                )
            }
        }
    }
}

@Composable
fun RepositoryScreenDataListState(
    onDirClick: (Map<String, Any?>) -> Unit,
    data: List<Content>,
    firstVisibleItemIndex: Int = 0,
    firstVisibleItemScrollOffset: Int = 0,
    onListScrollPositionChanged: (Int, Int) -> Unit,
    paddingValues: PaddingValues,
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = firstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = firstVisibleItemScrollOffset
    )

    LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }, remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }) {
        snapshotFlow {
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
        }
            .debounce(500L)
            .collectLatest { (index, offset) ->
                onListScrollPositionChanged(index, offset)
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),
    ) {
        itemsIndexed(data) { _, item ->
            if (item.type == ContentType.DIR) {
                Dir(onDirClick = onDirClick, item)
            } else {
                File(item)
            }
        }
    }
}


@Composable
fun File(file: Content) {
    val context = LocalContext.current

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Column {
        Row(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(4.dp)
                .clickable { openUrl(file.htmlUrl) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                modifier = Modifier.padding(horizontal = 8.dp),
                painter = painterResource(id = R.drawable.file_icon),
                contentDescription = "File icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(text = file.name, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                text = file.size
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun Dir(
    onDirClick: (Map<String, Any?>) -> Unit,
    dir: Content,
) {
    Column(
        Modifier.clickable {
            onDirClick(
                mapOf(
                    //NavArgs.REPOSITORY_ID to dir.name,
                    NavArgs.PATH to dir.path
                )
            )
        }
    ) {
        Row(modifier = Modifier.height(48.dp).fillMaxWidth()
            .background(MaterialTheme.colorScheme.background).padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.padding(horizontal = 8.dp),
                painter = painterResource(id = R.drawable.dir_icon),
                contentDescription = "Dir icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(text = dir.name, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold)
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}


