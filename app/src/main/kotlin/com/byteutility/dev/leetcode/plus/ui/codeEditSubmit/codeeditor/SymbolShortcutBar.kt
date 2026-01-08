package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.codeeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SymbolShortcutBar(onSymbolClick: (String) -> Unit) {
    val symbols = listOf("{", "}", "(", ")", "[", "]", ";", "=", ".", ",", "<", ">", "\"")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(symbols) { symbol ->
            TextButton(
                onClick = { onSymbolClick(symbol) },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = symbol,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF498A5C), // Your Emerald Green
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
