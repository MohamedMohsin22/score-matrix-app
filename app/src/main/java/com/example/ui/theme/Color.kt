package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Premier League Signature & Editorial Aesthetic Palette
val PlPlumBackground = Color(0xFF38003C) // Exact Design HTML body #38003c
val PlPlumHeaderStart = Color(0xFF3D195B) // Exact Design HTML gradient start #3d195b
val PlPlumSurface = Color(0xFF2E0032)
val PlPlumCard = Color(0xFF45064A)
val PlPlumCardElevated = Color(0xFF520E57)
val PlPlumBorder = Color(0xFF6B1B72)

val PlMint = Color(0xFF00FF85)          // Exact Design HTML Neon Green #00ff85
val PlCyan = Color(0xFF04F5FF)          // Exact Design HTML Electric Cyan #04f5ff
val PlPink = Color(0xFFFF2882)          // Signature PL Vibrant Pink
val PlYellow = Color(0xFFFFE500)        // Captain Gold / Accent
val PlPurplePrimary = Color(0xFF38003C) // Iconic PL Purple #38003c

// Editorial Translucent and Card Colors
val PlCardWhite = Color(0xFFFFFFFF)
val PlTextOnWhite = Color(0xFF38003C)
val PlCardDarkFrosted = Color(0x1AFFFFFF) // bg-white/10
val PlCardDarkBorder = Color(0x14FFFFFF)  // border-white/5
val PlScoreBgWhite = Color(0x0D38003C)    // #38003c/5
val PlScoreBorderWhite = Color(0x1A38003C)// #38003c/10

val PlTextPrimary = Color(0xFFFFFFFF)
val PlTextSecondary = Color(0xFFD4B8DB)
val PlTextTertiary = Color(0xFFA686AF)

val PlSuccess = Color(0xFF00FF85)
val PlWarning = Color(0xFFFFB703)
val PlError = Color(0xFFFF3366)
val PlChipBg = Color(0xFF4A0A50)

// Official Premier League Club Colors
fun getClubColor(code: String): Color = when (code.uppercase()) {
    "ARS" -> Color(0xFFEF0107)
    "MCI" -> Color(0xFF6CADDF)
    "LIV" -> Color(0xFFC8102E)
    "CHE" -> Color(0xFF034694)
    "MUN" -> Color(0xFFDA291C)
    "TOT" -> Color(0xFF132257)
    "NEW" -> Color(0xFF241F20)
    "AVL" -> Color(0xFF670E36)
    "BHA" -> Color(0xFF0057B8)
    "WHU" -> Color(0xFF7A263A)
    "EVE" -> Color(0xFF003399)
    "NFO", "NOT" -> Color(0xFFDD0000)
    "WOL" -> Color(0xFFFDB913)
    "BOU" -> Color(0xFFDA291C)
    "BRE" -> Color(0xFFD20000)
    "CRY" -> Color(0xFF1B458F)
    "FUL" -> Color(0xFF242424)
    "LEI" -> Color(0xFF003090)
    "IPS" -> Color(0xFF00448A)
    "SOU" -> Color(0xFFD71920)
    "HUL" -> Color(0xFFF59B00)
    "SUN" -> Color(0xFFEB172B)
    "COV" -> Color(0xFF5897D6)
    "LEE" -> Color(0xFF00539F)
    else -> Color(0xFF38003C)
}

fun getClubTextColor(code: String): Color = when (code.uppercase()) {
    "WOL", "HUL" -> Color(0xFF1F1F1F)
    else -> Color.White
}
