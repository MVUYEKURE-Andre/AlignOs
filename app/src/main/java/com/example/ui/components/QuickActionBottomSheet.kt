package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber400
import com.example.ui.theme.AmberBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Indigo400
import com.example.ui.theme.IndigoBg
import com.example.ui.theme.Rose400
import com.example.ui.theme.RoseBg
import com.example.ui.theme.Sky400
import com.example.ui.theme.SkyBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900

enum class QuickActionType {
  EXPENSE,
  INCOME,
  FOCUS_TIMER,
  SLEEP,
  REFLECTION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionBottomSheet(
  sheetState: SheetState,
  onDismiss: () -> Unit,
  onSelectAction: (QuickActionType) -> Unit
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Slate900,
    contentColor = Slate100,
    dragHandle = {
      Box(
        modifier = Modifier
          .padding(vertical = 12.dp)
          .width(44.dp)
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(Slate800)
      )
    }
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
    ) {
      Text(
        text = "Quick Alignment Actions",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          color = Slate100
        )
      )
      Text(
        text = "Log habit data or record a life pillar in seconds",
        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
        modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
      )

      QuickActionButtonRow(
        title = "Log Expense",
        subtitle = "Track daily outlay against stipend",
        icon = Icons.Filled.TrendingDown,
        iconTint = Rose400,
        iconBg = RoseBg,
        testTag = "quick_action_expense",
        onClick = {
          onDismiss()
          onSelectAction(QuickActionType.EXPENSE)
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      QuickActionButtonRow(
        title = "Record Income",
        subtitle = "Stipend, freelance, or deposit",
        icon = Icons.Filled.TrendingUp,
        iconTint = Emerald400,
        iconBg = EmeraldBg,
        testTag = "quick_action_income",
        onClick = {
          onDismiss()
          onSelectAction(QuickActionType.INCOME)
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      QuickActionButtonRow(
        title = "Start Focus Timer",
        subtitle = "Deep work, coding, or interview prep",
        icon = Icons.Filled.Timer,
        iconTint = Indigo400,
        iconBg = IndigoBg,
        testTag = "quick_action_timer",
        onClick = {
          onDismiss()
          onSelectAction(QuickActionType.FOCUS_TIMER)
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      QuickActionButtonRow(
        title = "Log Overnight Sleep",
        subtitle = "Sleep duration, wake time & recovery score",
        icon = Icons.Filled.Bedtime,
        iconTint = Sky400,
        iconBg = SkyBg,
        testTag = "quick_action_sleep",
        onClick = {
          onDismiss()
          onSelectAction(QuickActionType.SLEEP)
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      QuickActionButtonRow(
        title = "Quick Daily Reflection",
        subtitle = "Energy level, mood emoji & highlights",
        icon = Icons.Filled.Create,
        iconTint = Amber400,
        iconBg = AmberBg,
        testTag = "quick_action_reflection",
        onClick = {
          onDismiss()
          onSelectAction(QuickActionType.REFLECTION)
        }
      )
    }
  }
}

@Composable
private fun QuickActionButtonRow(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  iconBg: Color,
  testTag: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Slate850)
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 12.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .clip(CircleShape)
        .background(iconBg),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = iconTint,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.SemiBold,
          color = Slate100
        )
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
      )
    }
  }
}
