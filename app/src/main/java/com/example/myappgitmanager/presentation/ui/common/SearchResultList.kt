package com.example.myappgitmanager.presentation.ui.common

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myappgitmanager.R
import com.example.myappgitmanager.navigation.args.NavArgs
import com.example.myappgitmanager.presentation.models.Repo
import com.example.myappgitmanager.presentation.models.User

/*
@Preview()
@Composable
fun MultiTypeListPreview() {
    MultiTypeList()
}
 */

@Composable
fun MultiTypeList(
    onRepositoryClick: (Map<String, Any?>) -> Unit,
    paddingValues: PaddingValues,
    data: List<Any>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(data) { _, item ->
            when (item) {
                is Repo -> Repository(item, onItemClick = onRepositoryClick)
                is User -> User(item)
            }
        }
    }
}

@Composable
fun Repository(
    item: Repo,
    onItemClick: (Map<String, Any?>) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(item.isCollapsedView) }

    val stargazerCountIcon = painterResource(id = R.drawable.stargazers_count)
    val forksCountIcon = painterResource(id = R.drawable.forks_count)
    val watchersCountIcon = painterResource(id = R.drawable.watchers_count)

    val stargazerCountPainter = remember { stargazerCountIcon }
    val forksCountPainter = remember { forksCountIcon }
    val watchersCountPainter = remember { watchersCountIcon }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            onItemClick(
                mapOf(
                    NavArgs.REPOSITORY_ID to item.name,
                    NavArgs.OWNER_ID to item.owner.login
                )
            )
        },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatItem(stargazerCountPainter, item.stargazersCount.toString())
                    StatItem(forksCountPainter, item.watchersCount.toString())
                    StatItem(watchersCountPainter, item.forksCount.toString())
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.clickable { item.isCollapsedView = !isExpanded; isExpanded = !isExpanded }
            ) {
                Text(
                    text = if (isExpanded) "Скрыть" else "Подробнее",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Image(
                    painter = painterResource(id = R.drawable.arrow_right),
                    contentDescription = "Arrow Icon",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (isExpanded) 90f else 0f),
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                User(item = item.owner, cardSurfaceColor = MaterialTheme.colorScheme.surfaceVariant)

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("Создан: ")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                append(item.createdAt)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append("Обновлён: ")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                append(item.updatedAt)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        modifier = modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                item.description?.let {
                    Text(
                        text = "Описание:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(icon: Painter, text: String) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(text = text, modifier = Modifier.padding(horizontal = 2.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
            Icon(painter = icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp).background(Color.Transparent),
            )
        }
    }
}

@Composable
fun User(
    item: User,
    modifier: Modifier = Modifier,
    cardSurfaceColor: Color = MaterialTheme.colorScheme.surface
) {
    val context = LocalContext.current

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { openUrl(item.htmlUrl) },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(containerColor = cardSurfaceColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (item.picture != null) {
                Image(
                    bitmap = item.picture.asImageBitmap(),
                    contentDescription = "Circular Image",
                    modifier = Modifier
                        .size(50.dp)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            Text(
                text = item.login,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "Arrow Icon",
                modifier = Modifier
                    .size(35.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}



