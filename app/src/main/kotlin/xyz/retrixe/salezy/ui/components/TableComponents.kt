package xyz.retrixe.salezy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.HeadTableCell(text: String, weight: Float) =
    Text(text = text, Modifier
        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp))
        .weight(weight)
        .padding(8.dp))

@Composable
fun RowScope.TableCell(text: String, weight: Float) =
    Text(text = text, Modifier.weight(weight).padding(8.dp))
