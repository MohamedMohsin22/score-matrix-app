package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Picture
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    fun createBitmapFromPicture(picture: Picture): Bitmap {
        val width = picture.width.coerceAtLeast(1)
        val height = picture.height.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawPicture(picture)
        return bitmap
    }

    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "gw_summary.png"
    ): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            if (!cachePath.exists()) {
                cachePath.mkdirs()
            }
            val file = File(cachePath, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareSummaryCard(
        context: Context,
        imageUri: Uri?,
        summaryText: String
    ) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            if (imageUri != null) {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, summaryText)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, summaryText)
            }
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Gameweek Summary").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }

    fun shareLeagueInvite(context: Context, leagueName: String, inviteCode: String) {
        val shareText = """
            🏆 Join my Premier League 2026/2027 Score Predictor Mini-League: '$leagueName'!
            
            Use Invite Code: $inviteCode
            Or join directly: plpredictor://join?code=$inviteCode
            
            Predict scores, use Super Captain & Safety Net chips, and battle for the Gameweek MVP title!
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share League Invite").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(shareIntent)
    }
}
