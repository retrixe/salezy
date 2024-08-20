package xyz.retrixe.salezy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(placeholder: String, query: String, onQueryChange: (String) -> Unit) {
    DockedSearchBar(
        modifier = Modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { onQueryChange(it) },
                expanded = false, onExpandedChange = {}, // No search result popout
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                leadingIcon = { Icon(imageVector = Icons.Filled.Search, "Search") },
                onSearch = { /* TODO (low priority): Backend search for efficiency with pagination */ }
            )
        },
        expanded = false, onExpandedChange = {}, // No search result popout
    ) {}
}
