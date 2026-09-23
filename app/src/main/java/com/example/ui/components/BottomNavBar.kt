package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

enum class AlignNavTab(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val tag: String
) {
  HOME("Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_home"),
  TODAY("Today", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircleOutline, "nav_today"),
  PLUS("Quick Add", Icons.Filled.Add, Icons.Filled.Add, "nav_plus"),
  FINANCE("Finance", Icons.Filled.MonetizationOn, Icons.Outlined.MonetizationOn, "nav_finance"),
  MENU("Menu", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz, "nav_menu")
}

@Composable
fun AlignBottomNavBar(
  currentTab: AlignNavTab,
  onTabSelected: (AlignNavTab) -> Unit,
  onQuickActionClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(Slate900)
      .windowInsetsPadding(WindowInsets.navigationBars)
  ) {
    // Top border line
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(Slate800)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(64.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      AlignNavTab.entries.forEach { tab ->
        if (tab == AlignNavTab.PLUS) {
          // Centered Floating Quick Action Button
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(Emerald400)
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Slate950),
                onClick = onQuickActionClick
              )
              .testTag("fab_quick_action"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.Add,
              contentDescription = "Quick Add Modal",
              tint = Slate950,
              modifier = Modifier.size(28.dp)
            )
          }
        } else {
          val isSelected = tab == currentTab
          Column(
            modifier = Modifier
              .weight(1f)
              .clip(MaterialTheme.shapes.small)
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = { onTabSelected(tab) }
              )
              .padding(vertical = 6.dp)
              .testTag(tab.tag),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
              contentDescription = tab.label,
              tint = if (isSelected) Emerald400 else Slate400,
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = tab.label,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
              color = if (isSelected) Emerald400 else Slate400,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }
    }
  }
}
