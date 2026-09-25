package com.example.householdlocator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
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
    var statusMessage by remember { mutableStateOf("Search for an item or create one.") }
    var matchedItem by remember { mutableStateOf<HouseholdItem?>(null) }
    var locationResults by remember { mutableStateOf<List<HouseholdItem>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Household Item Locator", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(itemName, { itemName = it }, label = { Text("Item name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(itemLocation, { itemLocation = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        scope.launch {
                            val name = normalize(itemName)
                            val item = if (name.isEmpty()) null else dao.getByName(name)
                            matchedItem = item
                            statusMessage = if (item == null) "No item named '$itemName' was found." else "Found '${item.name}' in '${item.location}'."
                        }
                    }) { Text("Check Item") }
                    Button(onClick = {
                        scope.launch {
                            val name = itemName.trim()
                            val location = itemLocation.trim()
                            if (name.isEmpty() || location.isEmpty()) {
                                statusMessage = "Enter both an item name and a location."
                                return@launch
                            }
                            val existing = dao.getByName(normalize(name))
                            if (existing == null) {
                                val created = HouseholdItem(name = titleCase(name), location = titleCase(location))
                                dao.insert(created)
                                matchedItem = created
                                statusMessage = "Created '${created.name}' in '${created.location}'."
                            } else {
                                val updated = existing.copy(location = titleCase(location), updatedAt = System.currentTimeMillis())
                                dao.update(updated)
                                matchedItem = updated
                                statusMessage = "Updated '${updated.name}' to '${updated.location}'."
                            }
                        }
                    }) { Text("Save / Update") }
                }
                matchedItem?.let { Text("Current match: ${it.name} — ${it.location}", fontWeight = FontWeight.Medium) }
                Text(statusMessage)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(locationQuery, { locationQuery = it }, label = { Text("Search by location") }, modifier = Modifier.fillMaxWidth())
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            val query = normalize(locationQuery)
                            if (query.isEmpty()) {
                                locationResults = emptyList()
                                statusMessage = "Enter a location to search."
                            } else {
                                locationResults = dao.findByLocationOnce(query)
                                statusMessage = "Found ${locationResults.size} item(s) in '$locationQuery'."
                            }
                        }
                    }
                ) { Text("Find Items in Location") }
                if (locationResults.isEmpty()) Text("No items found for this location.")
                else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(locationResults, key = { it.id }) { item -> ItemCard(item) }
                }
            }
        }

        Text("All tracked household items", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (allItems.isEmpty()) Text("No household items recorded yet.")
        else LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allItems, key = { it.id }) { item -> ItemCard(item) }
        }
    }
}

@Composable
private fun ItemCard(item: HouseholdItem) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.name, fontWeight = FontWeight.Bold)
            Text("Location: ${item.location}")
        }
    }
}

private fun normalize(value: String): String = value.trim().lowercase(Locale.getDefault())

private fun titleCase(value: String): String = value.trim().split(Regex("\\s+"))
    .joinToString(" ") { word -> word.lowercase(Locale.getDefault()).replaceFirstChar { it.titlecase(Locale.getDefault()) } }
