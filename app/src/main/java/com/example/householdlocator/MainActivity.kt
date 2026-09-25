package com.example.householdlocator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.householdlocator.ui.HouseholdApp
import com.example.householdlocator.ui.theme.HouseholdItemLocatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HouseholdItemLocatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    HouseHoldDatabaseProvider.getInstance(context)
                    HouseholdApp(database = HouseHoldDatabaseProvider.getInstance(context))
                }
            }
        }
    }
}

object HouseHoldDatabaseProvider {
    @Volatile
    private var INSTANCE: com.example.householdlocator.data.HouseholdItemDatabase? = null

    fun getInstance(context: android.content.Context): com.example.householdlocator.data.HouseholdItemDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = com.example.householdlocator.data.HouseholdItemDatabase.getInstance(context)
            INSTANCE = instance
            instance
        }
    }
}
