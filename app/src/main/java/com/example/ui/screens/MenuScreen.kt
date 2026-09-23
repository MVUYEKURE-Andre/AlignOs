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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.Amber400
import com.example.ui.theme.AmberBg
import com.example.ui.theme.Emerald400
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Indigo400
import com.example.ui.theme.IndigoBg
import com.example.ui.theme.Sky400
import com.example.ui.theme.SkyBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.AlignViewModel

enum class SecondaryDestination {
  TIMER,
  SLEEP,
  FUTURE_TASKS,
  REPORTS,
  REFLECTIONS_ARCHIVE
}

@Composable
fun MenuScreen(
  viewModel: AlignViewModel,
  onNavigateToSecondary: (SecondaryDestination) -> Unit,
  onLogoutClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val userConfig by viewModel.userConfig.collectAsStateWithLifecycle()
  val currentUserEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()
  val currentUserName by viewModel.currentUserName.collectAsStateWithLifecycle()

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
        text = "AlignOS Hub",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Slate50
        )
      )
      Text(
        text = "Personal Life Operating System & specialized tools",
        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
      )
    }

    // User Profile Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(EmeraldBg)
                .border(1.5.dp, Emerald400, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = currentUserName.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Emerald400
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = currentUserName,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Slate50
                )
              )
              Text(
                text = currentUserEmail,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
              )
            }

            Icon(
              imageVector = Icons.Filled.CloudDone,
              contentDescription = "Synced",
              tint = Emerald400,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Sign Out Action Button
          Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = Slate850,
              contentColor = Emerald400
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald400.copy(alpha = 0.4f)),
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("btn_menu_logout")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Sign Out",
                tint = Emerald400,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Sign Out of AlignOS",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }

    // Hub Navigation Items
    item {
      Text(
        text = "Pillar Engines & Tools",
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          color = Slate100
        )
      )
    }

    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        HubNavigationItem(
          title = "Focus Timer & Deep Work",
          subtitle = "Stopwatch, Pomodoro intervals & study logs",
          icon = Icons.Filled.Timer,
          iconTint = Indigo400,
          iconBg = IndigoBg,
          testTag = "menu_item_timer",
          onClick = { onNavigateToSecondary(SecondaryDestination.TIMER) }
        )

        HubNavigationItem(
          title = "Sleep & Overnight Recovery",
          subtitle = "Bed/wake logs, sleep debt & quality scores",
          icon = Icons.Filled.Bedtime,
          iconTint = Sky400,
          iconBg = SkyBg,
          testTag = "menu_item_sleep",
          onClick = { onNavigateToSecondary(SecondaryDestination.SLEEP) }
        )

        HubNavigationItem(
          title = "Future Tasks & Interview Pipeline",
          subtitle = "Scheduling, countdown badges & interview prep",
          icon = Icons.Filled.EventNote,
          iconTint = Amber400,
          iconBg = AmberBg,
          testTag = "menu_item_future_tasks",
          onClick = { onNavigateToSecondary(SecondaryDestination.FUTURE_TASKS) }
        )

        HubNavigationItem(
          title = "Monthly Intelligence & Scorecard",
          subtitle = "Cross-pillar correlations & financial discipline",
          icon = Icons.Filled.Insights,
          iconTint = Emerald400,
          iconBg = EmeraldBg,
          testTag = "menu_item_intelligence",
          onClick = { onNavigateToSecondary(SecondaryDestination.REPORTS) }
        )

        HubNavigationItem(
          title = "Reflections & Journal Archive",
          subtitle = "Full-text search across past wins & realizations",
          icon = Icons.Filled.Bookmarks,
          iconTint = Indigo400,
          iconBg = IndigoBg,
          testTag = "menu_item_reflections_archive",
          onClick = { onNavigateToSecondary(SecondaryDestination.REFLECTIONS_ARCHIVE) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun HubNavigationItem(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: androidx.compose.ui.graphics.Color,
  iconBg: androidx.compose.ui.graphics.Color,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .border(1.dp, Slate800, RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
    colors = CardDefaults.cardColors(containerColor = Slate900)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
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

      Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
        contentDescription = "Navigate",
        tint = Slate700,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}
