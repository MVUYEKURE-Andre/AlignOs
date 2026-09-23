package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.util.DateUtils

@Composable
fun DateNavigator(
  selectedDate: String,
  onDateChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val isToday = DateUtils.isToday(selectedDate)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(Slate850)
      .border(1.dp, Slate800, RoundedCornerShape(16.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    IconButton(
      onClick = { onDateChange(DateUtils.addDays(selectedDate, -1)) },
      modifier = Modifier
        .size(40.dp)
        .testTag("btn_prev_day")
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
        contentDescription = "Previous Day",
        tint = Slate400,
        modifier = Modifier.size(16.dp)
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .clickable { onDateChange(DateUtils.today()) }
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .testTag("btn_jump_today")
    ) {
      if (isToday) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Emerald400)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = if (isToday) "Today (${DateUtils.formatDate(selectedDate)})" else DateUtils.formatDate(selectedDate),
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.SemiBold,
          color = if (isToday) Emerald400 else Slate100,
          fontSize = 13.sp
        )
      )
      if (!isToday) {
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
          imageVector = Icons.Filled.Today,
          contentDescription = "Return to Today",
          tint = Emerald400,
          modifier = Modifier.size(14.dp)
        )
      }
    }

    IconButton(
      onClick = { onDateChange(DateUtils.addDays(selectedDate, 1)) },
      modifier = Modifier
        .size(40.dp)
        .testTag("btn_next_day")
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = "Next Day",
        tint = Slate400,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
