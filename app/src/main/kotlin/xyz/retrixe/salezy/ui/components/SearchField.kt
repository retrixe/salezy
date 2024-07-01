package xyz.retrixe.salezy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(placeholder: String, query: String, onQueryChange: (String) -> Unit) {
    DockedSearchBar(
        query = query,
        onQueryChange = { onQueryChange(it) },
        active = false, onActiveChange = {}, // No search result popout
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, "Search") },
        onSearch = { /* TODO (low priority): Backend search for efficiency with pagination */ }
    ) {}
}
