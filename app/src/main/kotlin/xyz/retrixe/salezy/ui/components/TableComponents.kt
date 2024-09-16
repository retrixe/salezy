package xyz.retrixe.salezy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.HeadTableCell(text: String, weight: Float) =
    Text(text = text, Modifier
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
        .weight(weight)
        .padding(8.dp))

@Composable
fun HeadTableCell(text: String, width: Dp) =
    Text(text = text, Modifier
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
        .widthIn(min = width)
        .padding(8.dp))

@Composable
fun RowScope.TableCell(text: String, weight: Float, overflow: Boolean = true) =
    Text(
        text = text,
        // TODO (low priority): Should we overflow by default?
        maxLines = if (overflow) Int.MAX_VALUE else 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.weight(weight).padding(8.dp))
