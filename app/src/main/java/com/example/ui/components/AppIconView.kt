package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DeepIndigo
import com.example.ui.theme.MainBackground
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.ElevatedSurface
import com.example.ui.theme.PrimaryPurple
import com.example.ui.theme.PrimaryText

@Composable
fun AppIconView(
    iconKey: String,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    cloneBadgeNumber: Int? = null,
    isPrivateSpace: Boolean = false,
    packageName: String? = null
) {
    val context = LocalContext.current
    val targetPkg = packageName ?: if (iconKey.contains('.')) iconKey else null
    val realAppBitmap = remember(targetPkg) {
        if (targetPkg != null) {
            try {
                val pm = context.packageManager
                val drawable = pm.getApplicationIcon(targetPkg)
                val w = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
                val h = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128
                val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                drawable.setBounds(0, 0, w, h)
                drawable.draw(canvas)
                bmp.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val cornerRadius = size * 0.28f

        if (realAppBitmap != null) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(ElevatedSurface)
                    .border(1.dp, BorderColor, RoundedCornerShape(cornerRadius)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = realAppBitmap,
                    contentDescription = iconKey,
                    modifier = Modifier.size(size * 0.82f)
                )
            }
        } else {
            when (iconKey.lowercase()) {
            "whatsapp" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF25D366), Color(0xFF128C7E))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.58f)
                    )
                }
            }
            "instagram" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF833AB4),
                                    Color(0xFFFD1D1D),
                                    Color(0xFFFCB045)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(size * 0.55f)) {
                        val strokeW = this.size.width * 0.12f
                        drawRoundRect(
                            color = Color.White,
                            size = this.size,
                            cornerRadius = CornerRadius(this.size.width * 0.3f),
                            style = Stroke(width = strokeW)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = this.size.width * 0.25f,
                            center = center,
                            style = Stroke(width = strokeW)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = strokeW * 0.7f,
                            center = Offset(this.size.width * 0.78f, this.size.height * 0.22f)
                        )
                    }
                }
            }
            "facebook" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1877F2), Color(0xFF0C54B7))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "f",
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.65f).sp,
                        color = Color.White,
                        modifier = Modifier.offset(x = 1.dp, y = (-2).dp)
                    )
                }
            }
            "telegram" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2AABEE), Color(0xFF229ED9))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Telegram",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.52f).offset(x = (-1).dp, y = 1.dp)
                    )
                }
            }
            "tiktok" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(Color(0xFF010101))
                        .border(1.dp, BorderColor, RoundedCornerShape(cornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "TikTok",
                        tint = CyanAccent,
                        modifier = Modifier.size(size * 0.6f)
                    )
                }
            }
            "discord" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF5865F2), Color(0xFF4752C4))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Discord",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.58f)
                    )
                }
            }
            "x_twitter" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(Color.Black)
                        .border(1.dp, BorderColor, RoundedCornerShape(cornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "𝕏",
                        fontWeight = FontWeight.Black,
                        fontSize = (size.value * 0.52f).sp,
                        color = Color.White
                    )
                }
            }
            "messenger" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF00B2FF), Color(0xFF006AFF), Color(0xFFA033FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = "Messenger",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }
            "clash_of_clans" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFFF9900), Color(0xFFD46200))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Games,
                        contentDescription = "Clash of Clans",
                        tint = Color.White,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }
            "roblox" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(Color(0xFF1E1F22))
                        .border(1.dp, BorderColor, RoundedCornerShape(cornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "R",
                        fontWeight = FontWeight.Black,
                        fontSize = (size.value * 0.58f).sp,
                        color = Color.White
                    )
                }
            }
            "snapchat" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(Color(0xFFFFFC00)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👻",
                        fontSize = (size.value * 0.52f).sp
                    )
                }
            }
            "reddit" -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(Color(0xFFFF4500)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤖",
                        fontSize = (size.value * 0.52f).sp
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryPurple, DeepIndigo)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = iconKey,
                        tint = CyanAccent,
                        modifier = Modifier.size(size * 0.55f)
                    )
                }
            }
        }
    }

        // Clone Badge (#1, #2 or lock indicator)
        if (cloneBadgeNumber != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(size * 0.44f)
                    .clip(CircleShape)
                    .background(
                        if (isPrivateSpace) DeepIndigo else PrimaryPurple
                    )
                    .border(1.5.dp, if (isPrivateSpace) CyanAccent else Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$cloneBadgeNumber",
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.22f).sp,
                    color = Color.White
                )
            }
        } else if (isPrivateSpace) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(size * 0.42f)
                    .clip(CircleShape)
                    .background(DeepIndigo)
                    .border(1.5.dp, CyanAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔒",
                    fontSize = (size.value * 0.20f).sp
                )
            }
        }
    }
}
