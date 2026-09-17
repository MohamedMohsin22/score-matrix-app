package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Filter2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChipEntity
import com.example.ui.theme.PlCyan
import com.example.ui.theme.PlMint
import com.example.ui.theme.PlPink
import com.example.ui.theme.PlTextSecondary
import com.example.ui.theme.PlTextTertiary
import com.example.ui.theme.PlYellow

/**
 * Compact 4-Chip Tactical Hub:
 * Displays all 4 chips simultaneously across the screen in a balanced single-row layout
 * [ Super Captain (SC) ] [ Safety Net (SN) ] [ Auto Captain (AC) ] [ Double Shot (DS) ]
 * using equal weight distribution (Modifier.weight(1f)) with compact 4.dp to 6.dp padding.
 */
@Composable
fun TacticalChipsHub(
    currentHalf: Int,
    superCaptainChip: ChipEntity?,
    safetyNetChip: ChipEntity?,
    autoCaptainChip: ChipEntity?,
    doubleShotChip: ChipEntity?,
    isSuperCaptainActive: Boolean,
    isSafetyNetActive: Boolean,
    isAutoCaptainActive: Boolean,
    isDoubleShotActive: Boolean,
    isDeadlineLocked: Boolean,
    onChipClicked: (chipType: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TACTICAL CHIPS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = PlMint
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Text(
                    text = "1 PER GW • HALF $currentHalf",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Single balanced row showing all 4 chips side-by-side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // 1. Super Captain (SC)
            CompactChipItem(
                title = "Super Cap",
                acronym = "SC",
                perk = "3x Cap",
                icon = Icons.Default.Bolt,
                badgeColor = PlPink,
                isUsed = superCaptainChip?.isUsed == true,
                usedGw = superCaptainChip?.usedInGw,
                isActiveThisGw = isSuperCaptainActive,
                isLocked = isDeadlineLocked,
                testTag = "chip_super_captain",
                onClick = { onChipClicked("SUPER_CAPTAIN") },
                modifier = Modifier.weight(1f)
            )

            // 2. Safety Net (SN)
            CompactChipItem(
                title = "Safety Net",
                acronym = "SN",
                perk = "3pt Floor",
                icon = Icons.Default.Security,
                badgeColor = PlCyan,
                isUsed = safetyNetChip?.isUsed == true,
                usedGw = safetyNetChip?.usedInGw,
                isActiveThisGw = isSafetyNetActive,
                isLocked = isDeadlineLocked,
                testTag = "chip_safety_net",
                onClick = { onChipClicked("SAFETY_NET") },
                modifier = Modifier.weight(1f)
            )

            // 3. Auto Captain (AC)
            CompactChipItem(
                title = "Auto Cap",
                acronym = "AC",
                perk = "2x Top",
                icon = Icons.Default.AutoAwesome,
                badgeColor = PlYellow,
                isUsed = autoCaptainChip?.isUsed == true,
                usedGw = autoCaptainChip?.usedInGw,
                isActiveThisGw = isAutoCaptainActive,
                isLocked = isDeadlineLocked,
                testTag = "chip_auto_captain",
                onClick = { onChipClicked("AUTO_CAPTAIN") },
                modifier = Modifier.weight(1f)
            )

            // 4. Double Shot (DS)
            CompactChipItem(
                title = "Double Shot",
                acronym = "DS",
                perk = "2 Preds",
                icon = Icons.Default.Filter2,
                badgeColor = PlMint,
                isUsed = doubleShotChip?.isUsed == true,
                usedGw = doubleShotChip?.usedInGw,
                isActiveThisGw = isDoubleShotActive,
                isLocked = isDeadlineLocked,
                testTag = "chip_double_shot",
                onClick = { onChipClicked("DOUBLE_SHOT") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CompactChipItem(
    title: String,
    acronym: String,
    perk: String,
    icon: ImageVector,
    badgeColor: Color,
    isUsed: Boolean,
    usedGw: Int?,
    isActiveThisGw: Boolean,
    isLocked: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isFaded = isUsed && !isActiveThisGw

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isActiveThisGw -> PlMint.copy(alpha = 0.18f)
                    isFaded -> Color.White.copy(alpha = 0.04f)
                    else -> Color.White.copy(alpha = 0.08f)
                }
            )
            .border(
                width = if (isActiveThisGw) 1.5.dp else 1.dp,
                color = when {
                    isActiveThisGw -> PlMint
                    isFaded -> Color.White.copy(alpha = 0.05f)
                    else -> Color.White.copy(alpha = 0.12f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isLocked && (!isUsed || isActiveThisGw), onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 3.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Badge Icon with acronym indicator
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActiveThisGw -> PlMint
                            isUsed -> Color.White.copy(alpha = 0.15f)
                            else -> badgeColor
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title ($acronym)",
                    tint = when {
                        isActiveThisGw -> Color(0xFF38003C)
                        isUsed -> Color.White.copy(alpha = 0.4f)
                        else -> Color(0xFF38003C)
                    },
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Shortened Title & Acronym
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFaded) PlTextTertiary else Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Perk
            Text(
                text = perk,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    isActiveThisGw -> PlMint
                    isFaded -> PlTextTertiary
                    else -> PlCyan
                },
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Subtle Status Indicator
            val statusText = when {
                isActiveThisGw -> "ACTIVE"
                isUsed -> "GW $usedGw"
                else -> "READY"
            }
            val statusColor = when {
                isActiveThisGw -> PlMint
                isUsed -> PlTextTertiary
                else -> Color.White.copy(alpha = 0.65f)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isActiveThisGw) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(PlMint)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = statusText,
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    maxLines = 1
                )
            }
        }
    }
}
