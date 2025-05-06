package com.example.mywathever


import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier



@Composable
fun ItemNameAutoCompleteField(
    newItemName: String,
    onNameChange: (String) -> Unit,
    onPriceAutoFill: (Double) -> Unit,
    viewModel: MainViewModel
) {
    // Track suggestions from the ViewModel.
    val suggestions by viewModel.shoppingItemSuggestions.collectAsStateWithLifecycle(emptyList())

    // Track whether the dropdown is open.
    var expanded by remember { mutableStateOf(false) }

    TextField(
        value = newItemName,
        onValueChange = {
            onNameChange(it)
            // Each time the user types, search items with partial match.
            viewModel.searchShoppingItems(it)
            expanded = true
        },
        label = { Text("Item Name") },
        modifier = Modifier.fillMaxWidth()
    )

    DropdownMenu(
        expanded = expanded && suggestions.isNotEmpty(),
        onDismissRequest = { expanded = false }
    ) {
        suggestions.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    // When a suggestion is clicked, fill the fields.
                    onNameChange(item.itemName)
                    onPriceAutoFill(item.price)
                    expanded = false
                },
                text = { Text("${item.itemName} (Price: \$${item.price})") }
            )
        }
    }
}
