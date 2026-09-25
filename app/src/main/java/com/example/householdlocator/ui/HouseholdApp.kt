package com.example.householdlocator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.householdlocator.data.HouseholdItem
import com.example.householdlocator.data.HouseholdItemDatabase
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun HouseholdApp(database: HouseholdItemDatabase) {
    val dao = database.householdItemDao()
    val allItems by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var itemName by remember { mutableStateOf("") }
    var itemLocation by remember { mutableStateOf("") }
    var locationQuery by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("Search for a household item or create one.") }
    var matchedItem by remember { mutableStateOf<HouseholdItem?>(null) }
    var locationResults by remember { mutableStateOf<List<HouseholdItem>>(emptyList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Household Item Locator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = itemLocation,
                    onValueChange = { itemLocation = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        scope.launch {
                            val normalizedName = normalizeText(itemName)
                            val item = if (normalizedName.isBlank()) null else dao.getByName(normalizedName)
                            matchedItem = item
                            statusMessage = if (item == null) {
                                "No item named '$itemName' was found in the record."
                            } else {
                                "Found '${item.name}' in '${item.location}'."
                            }
                        }
                    }) {
                        Text("Check Item")
                    }

                    Button(onClick = {
                        scope.launch {
                            val trimmedName = itemName.trim()
                            val trimmedLocation = itemLocation.trim()

                            if (trimmedName.isBlank() || trimmedLocation.isBlank()) {
                                statusMessage = "Please enter both an item name and a location."
                                return@launch
                            }

                            val normalizedName = normalizeText(trimmedName)
                            val existing = dao.getByName(normalizedName)

                            if (existing != null) {
                                val updated = existing.copy(
                                    location = formatTitle(trimmedLocation),
                                    updatedAt = System.currentTimeMillis()
                                )
                                dao.update(updated)
                                matchedItem = updated
                                statusMessage = "Updated '${updated.name}' to '${updated.location}'."
                            } else {
                                val created = HouseholdItem(
                                    name = formatTitle(trimmedName),
                                    location = formatTitle(trimmedLocation),
                                    createdAt = System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                dao.insert(created)
                                matchedItem = created
                                statusMessage = "Created new item '${created.name}' in '${created.location}'."
                            }
                        }
                    }) {
                        Text("Save / Update")
                    }
                }

                if (matchedItem != null) {
                    Text(
                        text = "Current match: ${matchedItem!!.name} — ${matchedItem!!.location}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(text = statusMessage)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = locationQuery,
                    onValueChange = { locationQuery = it },
                    label = { Text("Search by location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        scope.launch {
                            val query = normalizeText(locationQuery)
                            if (query.isBlank()) {
                                locationResults = emptyList()
                                statusMessage = "Enter a location to search for items."
                                return@launch
                            }

                            dao.getByLocation(query).collect { items ->
                                locationResults = items
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Find Items in Location")
                }

                if (locationResults.isEmpty()) {
                    Text("No items found for this location.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(locationResults) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = item.name, fontWeight = FontWeight.Bold)
                                    Text(text = "Location: ${item.location}")
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "All tracked household items",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (allItems.isEmpty()) {
            Text("No household items recorded yet.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = item.name, fontWeight = FontWeight.Bold)
                            Text(text = "Location: ${item.location}")
                        }
                    }
                }
            }
        }
    }
}

private fun normalizeText(value: String): String {
    return value.trim().lowercase(Locale.getDefault())
}

private fun formatTitle(value: String): String {
    return value.trim()
        .split(Regex("\\s+"))
        .joinToString(" ") { word ->
            word.lowercase(Locale.getDefault()).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
}
