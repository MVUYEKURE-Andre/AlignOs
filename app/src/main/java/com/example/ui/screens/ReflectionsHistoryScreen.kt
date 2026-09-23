package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DailyMood
import com.example.ui.theme.Amber400
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Indigo400
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel

@Composable
fun ReflectionsHistoryScreen(
  viewModel: AlignViewModel,
  modifier: Modifier = Modifier
) {
  val allReflections by viewModel.allReflections.collectAsStateWithLifecycle()
  var searchQuery by remember { mutableStateOf("") }

  val filteredReflections = if (searchQuery.isBlank()) {
    allReflections
  } else {
    allReflections.filter {
      it.howDayWent.contains(searchQuery, ignoreCase = true) ||
        it.highlights.contains(searchQuery, ignoreCase = true) ||
        it.challenges.contains(searchQuery, ignoreCase = true) ||
        it.mood.contains(searchQuery, ignoreCase = true)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Reflections & Journal",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Slate50
        )
      )
      Text(
        text = "Searchable personal archive, mood patterns, and retrospective",
        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
        modifier = Modifier.padding(bottom = 6.dp)
      )

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search past thoughts, wins, challenges...") },
        leadingIcon = {
          Icon(Icons.Filled.Search, contentDescription = "Search", tint = Slate400)
        },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Slate400)
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("input_search_reflections"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = Emerald400,
          unfocusedBorderColor = Slate800,
          focusedTextColor = Slate100,
          unfocusedTextColor = Slate100
        )
      )
    }

    // Mood Summary Breakdown
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Mood Distribution", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Slate100))
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            DailyMood.entries.forEach { mood ->
              val count = allReflections.count { it.mood.equals(mood.name, ignoreCase = true) }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(mood.emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "$count",
                  fontWeight = FontWeight.Bold,
                  color = Slate100,
                  fontSize = 12.sp
                )
                Text(
                  text = mood.label,
                  color = Slate400,
                  fontSize = 10.sp
                )
              }
            }
          }
        }
      }
    }

    item {
      Text(
        text = "Reflections Log (${filteredReflections.size})",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Slate100
        )
      )
    }

    if (filteredReflections.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Slate900)
            .padding(24.dp),
          contentAlignment = Alignment.Center
        ) {
          Text("No reflection entries found.", color = Slate500, fontSize = 13.sp)
        }
      }
    } else {
      items(filteredReflections, key = { it.id }) { reflection ->
        val mood = DailyMood.fromString(reflection.mood)
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(mood.emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = reflection.date,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Slate100
                    )
                  )
                  Text(
                    text = "${mood.label} • Energy ${reflection.energyLevel}/5",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                  )
                }
              }

              Row {
                for (i in 1..reflection.energyLevel) {
                  Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Amber400,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }

            if (reflection.howDayWent.isNotBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = reflection.howDayWent,
                style = MaterialTheme.typography.bodyMedium.copy(color = Slate300)
              )
            }

            if (reflection.highlights.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Row {
                Text("Win: ", color = Emerald400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(reflection.highlights, color = Slate300, fontSize = 12.sp)
              }
            }

            if (reflection.challenges.isNotBlank()) {
              Spacer(modifier = Modifier.height(4.dp))
              Row {
                Text("Challenge: ", color = Amber400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(reflection.challenges, color = Slate300, fontSize = 12.sp)
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
