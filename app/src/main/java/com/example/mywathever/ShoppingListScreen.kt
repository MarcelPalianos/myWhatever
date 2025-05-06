package com.example.mywathever

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mywathever.data.ShoppingItemEntity

@Composable
fun ShoppingListScreen(
    viewModel: MainViewModel,
    goalId: Int
) {
    val shoppingItems by viewModel.getShoppingItemsForGoal(goalId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var newItemName by remember { mutableStateOf("") }
    var newItemAmount by remember { mutableStateOf("") }
    var newItemPrice by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Replace h6 with titleLarge or whichever M3 style you prefer
        Text(
            text = "Shopping List (Goal ID: $goalId)",
            style = MaterialTheme.typography.titleLarge
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(shoppingItems) { item ->
                ShoppingItemRow(item = item)
            }
        }

        // Material 3 Divider
        Divider(thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        ItemNameAutoCompleteField(
            newItemName = newItemName,
            onNameChange = { newItemName = it },
            onPriceAutoFill = { autoPrice -> newItemPrice = autoPrice.toString() },
            viewModel = viewModel // your current MainViewModel instance
        )

        TextField(
            value = newItemName,
            onValueChange = { newItemName = it },
            label = { Text("Item Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = newItemAmount,
            onValueChange = { newItemAmount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = newItemPrice,
            onValueChange = { newItemPrice = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val amountInt = newItemAmount.toIntOrNull() ?: 0
                val priceDouble = newItemPrice.toDoubleOrNull() ?: 0.0
                viewModel.insertShoppingItem(
                    goalId = goalId,
                    itemName = newItemName,
                    amount = amountInt,
                    price = priceDouble
                )
                newItemName = ""
                newItemAmount = ""
                newItemPrice = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Item")
        }
    }
}

@Composable
fun ShoppingItemRow(item: ShoppingItemEntity) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
    ) {
        // Replace body1 with bodyLarge or bodyMedium
        Text(
            text = item.itemName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        // Replace body2 with bodyMedium or bodySmall
        Text(
            text = "Qty: ${item.amount}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Price: \$${item.price}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
// Inside your ShoppingListScreen composable:

}
