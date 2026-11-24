package com.microalert.app.presentation.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate as rotateModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import com.microalert.app.domain.model.RiskLevel

object Neon {
    var isDark by mutableStateOf(true)

    val Void get() = if (isDark) Color(0xFF04050B) else Color(0xFFF3F4FB)
    val Ink get() = if (isDark) Color(0xFF0B0F22) else Color(0xFFE4E8F7)
    val Panel get() = if (isDark) Color(0xFF131936) else Color(0xFFFFFFFF)
    val Cyan get() = if (isDark) Color(0xFF00F0FF) else Color(0xFF008FA3)
    val Violet get() = if (isDark) Color(0xFF9B5CFF) else Color(0xFF6A3DD8)
    val Magenta get() = if (isDark) Color(0xFFFF2BD6) else Color(0xFFC2185B)
    val Lime get() = if (isDark) Color(0xFF39FF9A) else Color(0xFF00965A)
    val Yellow get() = if (isDark) Color(0xFFFFE14D) else Color(0xFFA67C00)
    val Amber get() = if (isDark) Color(0xFFFF8A1F) else Color(0xFFD65A00)
    val Red get() = if (isDark) Color(0xFFFF2D55) else Color(0xFFD50032)
    val TextHi get() = if (isDark) Color(0xFFF2F4FF) else Color(0xFF12162B)
    val TextLo get() = if (isDark) Color(0xFF8C93B8) else Color(0xFF555C80)
}

val RiskLevel.neon: Color
    get() = when (this) {
        RiskLevel.NORMAL -> Neon.Lime
        RiskLevel.PRECAUTION -> Neon.Yellow
        RiskLevel.HIGH -> Neon.Amber
        RiskLevel.CRITICAL -> Neon.Red
    }

private val RiskLevel.gaugeFill: Float
    get() = when (this) {
        RiskLevel.NORMAL -> 0.25f
        RiskLevel.PRECAUTION -> 0.5f
        RiskLevel.HIGH -> 0.75f
        RiskLevel.CRITICAL -> 1f
    }

@Composable
fun NeonBackdrop(
    accent: Color = Neon.Violet,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "backdrop")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift"
    )
    val animatedAccent by animateColorAsState(accent, tween(700), label = "accent")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Neon.Void)
            .drawBehind {
                val w = size.width
                val h = size.height
                val r = w * 0.75f
                val topCenter = Offset(w * (0.15f + 0.5f * drift), h * 0.05f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(animatedAccent.copy(alpha = if (Neon.isDark) 0.34f else 0.20f), Color.Transparent),
                        center = topCenter,
                        radius = r
                    ),
                    radius = r,
                    center = topCenter
                )
                val bottomCenter = Offset(w * (0.85f - 0.5f * drift), h * 0.98f)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Neon.Magenta.copy(alpha = if (Neon.isDark) 0.22f else 0.10f), Color.Transparent),
                        center = bottomCenter,
                        radius = r
                    ),
                    radius = r,
                    center = bottomCenter
                )
            },
        content = content
    )
}

@Composable
fun GlowText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Neon.TextHi,
    glow: Color = color,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    letterSpacing: TextUnit = 0.sp,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        letterSpacing = letterSpacing,
        textAlign = textAlign,
        maxLines = maxLines,
        style = TextStyle(shadow = if (Neon.isDark) Shadow(glow.copy(alpha = 0.75f), Offset.Zero, 9f) else null)
    )
}

@Composable
fun NeonTitle(text: String, modifier: Modifier = Modifier, accent: Color = Neon.Cyan) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        GlowText(
            text = text,
            color = accent,
            fontSize = 15.sp,
            letterSpacing = 3.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(3.dp))
        Box(
            Modifier
                .width(44.dp)
                .height(2.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, accent, Neon.Magenta, Color.Transparent)))
        )
    }
}

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    accent: Color = Neon.Cyan,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(listOf(Neon.Panel.copy(alpha = 0.92f), Neon.Ink.copy(alpha = 0.92f))))
            .border(
                1.2.dp,
                Brush.linearGradient(listOf(accent.copy(alpha = 0.95f), Neon.Magenta.copy(alpha = 0.35f), accent.copy(alpha = 0.15f))),
                shape
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        content = content
    )
}

@Composable
fun NeonRow(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NeonCard(modifier = modifier, accent = accent, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GlowDot(accent)
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = Neon.TextHi,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = value,
                color = accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun GlowDot(color: Color, size: Dp = 9.dp) {
    Box(
        Modifier
            .size(size)
            .drawBehind {
                drawCircle(
                    Brush.radialGradient(listOf(color.copy(alpha = 0.55f), Color.Transparent)),
                    radius = this.size.minDimension * 1.3f
                )
                drawCircle(color)
            }
    )
}

@Composable
fun StatTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Neon.Panel.copy(alpha = 0.85f))
            .border(1.dp, Brush.verticalGradient(listOf(accent.copy(alpha = 0.8f), accent.copy(alpha = 0.1f))), shape)
            .padding(horizontal = 6.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = accent,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            style = TextStyle(shadow = if (Neon.isDark) Shadow(accent.copy(alpha = 0.7f), Offset.Zero, 8f) else null)
        )
        Text(text = label, color = Neon.TextLo, fontSize = 9.sp, letterSpacing = 1.sp, maxLines = 1)
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Neon.Cyan, Neon.Violet)
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(colors))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Neon.Void,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(shape)
            .background(accent.copy(alpha = 0.12f))
            .border(1.3.dp, accent, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun PowerOrb(
    label: String,
    sublabel: String,
    live: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "orb")
    val spin by transition.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(if (live) 2200 else 6000, easing = LinearEasing)),
        label = "spin"
    )
    val breathe by transition.animateFloat(
        0.9f, 1.04f,
        infiniteRepeatable(tween(if (live) 700 else 1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )
    val core = if (live) Neon.Lime else Neon.Cyan
    val second = if (live) Neon.Cyan else Neon.Magenta

    Box(
        modifier = modifier
            .size(132.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .drawBehind {
                val c = center
                val base = size.minDimension / 2f
                drawCircle(
                    Brush.radialGradient(listOf(core.copy(alpha = 0.45f), Color.Transparent), c, base),
                    radius = base
                )
                val body = base * 0.74f * breathe
                drawCircle(
                    Brush.radialGradient(
                        listOf(core.copy(alpha = 0.30f), Neon.Ink, Neon.Void),
                        c,
                        body * 1.3f
                    ),
                    radius = body
                )
                rotate(spin, c) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, second, core, Color.Transparent),
                            c
                        ),
                        radius = body,
                        style = Stroke(5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                rotate(-spin * 0.6f, c) {
                    drawCircle(
                        color = core.copy(alpha = 0.55f),
                        radius = base * 0.94f,
                        style = Stroke(
                            1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 16f))
                        )
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlowText(
                text = label,
                color = Neon.TextHi,
                glow = core,
                fontSize = 17.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(text = sublabel, color = core, fontSize = 9.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun RiskGauge(
    level: RiskLevel,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val target = level.gaugeFill
    val progress by animateFloatAsState(target, tween(900, easing = FastOutSlowInEasing), label = "progress")
    val color by animateColorAsState(level.neon, tween(600), label = "gaugeColor")
    val transition = rememberInfiniteTransition(label = "gauge")
    val spin by transition.animateFloat(0f, 360f, infiniteRepeatable(tween(14000, easing = LinearEasing)), label = "spin")
    val pulse by transition.animateFloat(
        0.55f, 1f,
        infiniteRepeatable(tween(if (level == RiskLevel.CRITICAL) 450 else 1700), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .drawBehind {
                val stroke = 11.dp.toPx()
                val inset = stroke / 2f + 12.dp.toPx()
                val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
                val topLeft = Offset(inset, inset)

                drawCircle(
                    Brush.radialGradient(listOf(color.copy(alpha = 0.30f * pulse), Color.Transparent), center, size.minDimension / 2f),
                    radius = size.minDimension / 2f
                )
                rotate(spin, center) {
                    drawCircle(
                        color = color.copy(alpha = 0.45f),
                        radius = size.minDimension / 2f - 3.dp.toPx(),
                        style = Stroke(
                            1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 12f))
                        )
                    )
                }
                drawArc(
                    color = Color.White.copy(alpha = 0.09f),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
                rotate(135f, center) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to color.copy(alpha = 0.35f),
                            0.75f to color,
                            1f to color,
                            center = center
                        ),
                        startAngle = 0f,
                        sweepAngle = 270f * progress,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

@Composable
fun HeartbeatIcon(bpm: Float, color: Color, modifier: Modifier = Modifier, size: Dp = 22.dp) {
    val period = if (bpm > 30f) (60_000f / bpm).toInt().coerceIn(300, 2000) else 1000
    val transition = rememberInfiniteTransition(label = "heart")
    val scale by transition.animateFloat(
        0.82f, 1.12f,
        infiniteRepeatable(tween(period / 2, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "beat"
    )
    Canvas(modifier.size(size)) {
        val w = this.size.width * scale
        val h = this.size.height * scale
        val ox = (this.size.width - w) / 2f
        val oy = (this.size.height - h) / 2f
        val path = Path().apply {
            moveTo(ox + w / 2f, oy + h * 0.92f)
            cubicTo(ox - w * 0.25f, oy + h * 0.45f, ox + w * 0.10f, oy - h * 0.12f, ox + w / 2f, oy + h * 0.28f)
            cubicTo(ox + w * 0.90f, oy - h * 0.12f, ox + w * 1.25f, oy + h * 0.45f, ox + w / 2f, oy + h * 0.92f)
            close()
        }
        drawPath(path, color.copy(alpha = 0.35f), style = Stroke(6.dp.toPx(), join = StrokeJoin.Round))
        drawPath(path, color, style = Fill)
    }
}

enum class NeonGlyph { PULSE, CLOCK, SLIDERS, SHIELD }

@Composable
fun GlyphButton(
    glyph: NeonGlyph,
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Neon.Panel, Neon.Ink)))
                .border(1.3.dp, Brush.linearGradient(listOf(accent, Neon.Magenta.copy(alpha = 0.3f))), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(22.dp)) { drawGlyph(glyph, accent) }
        }
        Spacer(Modifier.height(3.dp))
        Text(text = label, color = Neon.TextLo, fontSize = 9.sp, letterSpacing = 0.5.sp, maxLines = 1)
    }
}

private fun DrawScope.drawGlyph(glyph: NeonGlyph, color: Color) {
    val w = size.width
    val h = size.height
    val stroke = Stroke(2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (glyph) {
        NeonGlyph.PULSE -> {
            val p = Path().apply {
                moveTo(0f, h * 0.55f)
                lineTo(w * 0.28f, h * 0.55f)
                lineTo(w * 0.40f, h * 0.15f)
                lineTo(w * 0.58f, h * 0.9f)
                lineTo(w * 0.70f, h * 0.55f)
                lineTo(w, h * 0.55f)
            }
            drawPath(p, color, style = stroke)
        }
        NeonGlyph.CLOCK -> {
            drawCircle(color, radius = w * 0.46f, style = stroke)
            drawLine(color, center, Offset(w / 2f, h * 0.22f), 2.2.dp.toPx(), StrokeCap.Round)
            drawLine(color, center, Offset(w * 0.74f, h * 0.6f), 2.2.dp.toPx(), StrokeCap.Round)
        }
        NeonGlyph.SLIDERS -> {
            val ys = listOf(h * 0.2f, h * 0.5f, h * 0.8f)
            val xs = listOf(w * 0.7f, w * 0.3f, w * 0.6f)
            ys.forEachIndexed { i, y ->
                drawLine(color.copy(alpha = 0.6f), Offset(0f, y), Offset(w, y), 2.dp.toPx(), StrokeCap.Round)
                drawCircle(color, radius = 3.4.dp.toPx(), center = Offset(xs[i], y))
            }
        }
        NeonGlyph.SHIELD -> {
            val p = Path().apply {
                moveTo(w / 2f, 0f)
                lineTo(w, h * 0.2f)
                lineTo(w * 0.9f, h * 0.65f)
                quadraticTo(w / 2f, h * 1.1f, w / 2f, h)
                quadraticTo(w / 2f, h * 1.1f, w * 0.1f, h * 0.65f)
                lineTo(0f, h * 0.2f)
                close()
            }
            drawPath(p, color, style = stroke)
        }
    }
}

@Composable
fun WarningGlyph(color: Color, modifier: Modifier = Modifier, size: Dp = 52.dp) {
    val transition = rememberInfiniteTransition(label = "shake")
    val angle by transition.animateFloat(
        -6f, 6f,
        infiniteRepeatable(tween(90, easing = LinearEasing), RepeatMode.Reverse),
        label = "angle"
    )
    Canvas(modifier.size(size).rotateModifier(angle)) {
        val w = this.size.width
        val h = this.size.height
        val tri = Path().apply {
            moveTo(w / 2f, h * 0.06f)
            lineTo(w * 0.97f, h * 0.9f)
            lineTo(w * 0.03f, h * 0.9f)
            close()
        }
        drawPath(tri, color.copy(alpha = 0.28f), style = Stroke(9.dp.toPx(), join = StrokeJoin.Round))
        drawPath(tri, color, style = Stroke(3.5.dp.toPx(), join = StrokeJoin.Round))
        drawLine(color, Offset(w / 2f, h * 0.36f), Offset(w / 2f, h * 0.62f), 4.dp.toPx(), StrokeCap.Round)
        drawCircle(color, radius = 2.6.dp.toPx(), center = Offset(w / 2f, h * 0.76f))
    }
}

@Composable
fun ExpandingRings(color: Color, modifier: Modifier = Modifier, fast: Boolean = false) {
    val transition = rememberInfiniteTransition(label = "rings")
    val t by transition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(if (fast) 1100 else 2200, easing = LinearEasing)),
        label = "t"
    )
    Canvas(modifier.fillMaxSize()) {
        val maxR = size.maxDimension * 0.75f
        for (i in 0 until 3) {
            val phase = (t + i / 3f) % 1f
            drawCircle(
                color = color.copy(alpha = (1f - phase) * 0.5f),
                radius = maxR * phase,
                style = Stroke(2.5.dp.toPx())
            )
        }
    }
}
