package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPink
import com.example.ui.theme.PlPlumBackground
import com.example.ui.theme.PlPlumBorder
import com.example.ui.theme.PlPlumCard
import com.example.ui.theme.PlPlumCardElevated
import com.example.ui.theme.PlTextPrimary
import com.example.ui.theme.PlTextSecondary
import com.example.ui.theme.PlTextTertiary
import com.example.ui.theme.PlYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesHelpBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PlPlumCard,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.3f)
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("dialog_rules_help")
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PlMint, PlCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PlPlumBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "OFFICIAL SCORING RULES",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Premier League Predictor Scoring Matrix",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = PlCyan
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_rules_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Rules",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Standard Match Points
            Text(
                text = "MATCH RESULT SCORING",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = PlTextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(
                icon = Icons.Default.CheckCircle,
                iconTint = PlMint,
                badgeText = "3 PTS",
                badgeColor = PlMint,
                badgeTextColor = PlPlumBackground,
                title = "Exact Score",
                description = "Predict the exact final score of the match (e.g., predicting 2-1 and match finishes 2-1)."
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(
                icon = Icons.Default.Shield,
                iconTint = PlCyan,
                badgeText = "+1 / +2 PTS",
                badgeColor = PlCyan,
                badgeTextColor = PlPlumBackground,
                title = "Decoupled Clean Sheet Bonus",
                description = "• +1 pt per team when predicted opponent score is 0 AND actual opponent score is 0.\n• Applies to BOTH exact scores and correct win outcomes.\n• Goalless Draw (0-0 Exact): 3 base + 2 Clean Sheets = 5 PTS total.\n• Win to nil (e.g., pred 2-0, ended 1-0 or 2-0): +1 Clean Sheet bonus."
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(
                icon = Icons.Default.Star,
                iconTint = PlYellow,
                badgeText = "2 PTS / 1 PT",
                badgeColor = PlYellow,
                badgeTextColor = Color.Black,
                title = "Correct Outcome (Non-Exact)",
                description = "• Correct Draw: 2 PTS (e.g., predicted 1-1, ended 2-2).\n• Correct Win: 1 PT (e.g., predicted 2-1, ended 1-0)."
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(
                icon = Icons.Default.Close,
                iconTint = PlPink,
                badgeText = "0 PTS",
                badgeColor = PlPlumCardElevated,
                badgeTextColor = PlTextTertiary,
                title = "Incorrect Outcome",
                description = "Wrong match winner or draw result. No points awarded."
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = PlPlumBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Captain & Chips
            Text(
                text = "CAPTAIN & CHIP MULTIPLIERS",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = PlTextTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(
                icon = Icons.Default.Star,
                iconTint = PlYellow,
                badgeText = "2X PTS",
                badgeColor = PlYellow,
                badgeTextColor = Color.Black,
                title = "Captain",
                description = "Double all match points earned on your selected fixture (base points + clean sheet bonuses)."
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(
                icon = Icons.Default.Bolt,
                iconTint = PlMint,
                badgeText = "3X PTS",
                badgeColor = PlMint,
                badgeTextColor = PlPlumBackground,
                title = "Super Captain Chip",
                description = "Triples all match points on 1 fixture. Replaces your standard 2x captain for that fixture. Single use per season!"
            )

            Spacer(modifier = Modifier.height(8.dp))

            RuleRow(
                icon = Icons.Default.Security,
                iconTint = PlCyan,
                badgeText = "3 PTS GUARANTEED",
                badgeColor = PlCyan,
                badgeTextColor = PlPlumBackground,
                title = "Safety Net Chip",
                description = "Insurance on 1 risky fixture: if your prediction scores 0 points, Safety Net automatically awards +3 points. Single use per season!"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_dismiss_rules_sheet"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PlMint,
                    contentColor = PlPlumBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Got It",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuleRow(
    icon: ImageVector,
    iconTint: Color,
    badgeText: String,
    badgeColor: Color,
    badgeTextColor: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = PlPlumCardElevated,
        border = BorderStroke(1.dp, PlPlumBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = PlTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
