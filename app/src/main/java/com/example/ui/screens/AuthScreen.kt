package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.EmeraldBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun AuthScreen(
  onLoginSuccess: (email: String, name: String) -> Unit,
  defaultEmail: String = "andremvuyekure@gmail.com",
  modifier: Modifier = Modifier
) {
  var email by remember { mutableStateOf(defaultEmail) }
  var password by remember { mutableStateOf("••••••••") }
  var isSubmitting by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(horizontal = 24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {

      // Glowing Emerald Brand Logo
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(EmeraldBg)
          .border(2.dp, Emerald400, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Filled.Shield,
          contentDescription = "AlignOS Shield",
          tint = Emerald400,
          modifier = Modifier.size(38.dp)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "AlignOS",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          color = Slate100,
          letterSpacing = (-0.5).sp
        )
      )

      Text(
        text = "Personal Life Operating System",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = Emerald400,
          fontWeight = FontWeight.Medium
        ),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(32.dp))

      // Auth Card (Pure Dark & Green)
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Text(
            text = "Sign In to Your Workspace",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Slate100
            )
          )

          // Email Input
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Account Email", color = Slate400) },
            leadingIcon = {
              Icon(Icons.Filled.Email, contentDescription = "Email", tint = Emerald400)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Email,
              imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Slate850,
              unfocusedContainerColor = Slate950,
              focusedBorderColor = Emerald400,
              unfocusedBorderColor = Slate800,
              focusedTextColor = Slate100,
              unfocusedTextColor = Slate300
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_email")
          )

          // Password Input
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Security PIN / Password", color = Slate400) },
            leadingIcon = {
              Icon(Icons.Filled.Lock, contentDescription = "Password", tint = Emerald400)
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                onLoginSuccess(email, name)
              }
            ),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Slate850,
              unfocusedContainerColor = Slate950,
              focusedBorderColor = Emerald400,
              unfocusedBorderColor = Slate800,
              focusedTextColor = Slate100,
              unfocusedTextColor = Slate300
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("input_password")
          )

          // Primary Sign In Button
          Button(
            onClick = {
              val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
              onLoginSuccess(email, name)
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Emerald500,
              contentColor = Slate950
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("btn_sign_in")
          ) {
            Text(
              text = "Sign In & Sync Data",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }

          // Quick 1-Tap Demo Sign In
          Button(
            onClick = {
              onLoginSuccess("andremvuyekure@gmail.com", "Andre Mvuyekure")
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = EmeraldBg,
              contentColor = Emerald400
            ),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald400.copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("btn_quick_demo_sign_in")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "⚡ Instant Access as Andre",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "Offline-First Encrypted Database • Room & SQLite",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Slate400,
          fontSize = 11.sp
        ),
        textAlign = TextAlign.Center
      )
    }
  }
}
