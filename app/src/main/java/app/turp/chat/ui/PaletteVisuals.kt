package app.turp.chat.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.turp.chat.R
import app.turp.chat.settings.ColorPalette
import app.turp.chat.ui.theme.PalettePreviewColors

@Composable
internal fun PaletteSwatch(colors: PalettePreviewColors, modifier: Modifier = Modifier) {
    Row(modifier) {
        PaletteDot(colors.primary)
        PaletteDot(colors.secondary, Modifier.offset(x = (-7).dp))
        PaletteDot(colors.tertiary, Modifier.offset(x = (-14).dp))
    }
}

@Composable
private fun PaletteDot(color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color,
        shape = CircleShape,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
        modifier = modifier.size(24.dp),
    ) {}
}

/** Palette used by every in-app Turp icon. It mirrors the launcher alias exactly. */
internal val LocalTurpIconPalette = staticCompositionLocalOf { ColorPalette.TURP }

/** Palette-matched Turp foreground mark used inside the app. */
@Composable
internal fun TurpMark(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val palette = LocalTurpIconPalette.current
    Image(
        painter = painterResource(palette.turpMarkDrawable),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

@Composable
internal fun LauncherIconPreview(
    palette: ColorPalette,
    size: Dp = 54.dp,
    modifier: Modifier = Modifier,
) {
    val foregroundInset = size * (22f / 108f)
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 1.dp,
        modifier = modifier.size(size),
    ) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clip(MaterialTheme.shapes.large),
        ) {
            Image(
                painter = painterResource(palette.launcherBackgroundDrawable),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
            )
            Image(
                painter = painterResource(palette.launcherForegroundDrawable),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .matchParentSize()
                    .padding(foregroundInset),
            )
        }
    }
}

@get:DrawableRes
internal val ColorPalette.turpMarkDrawable: Int
    get() = when (this) {
        ColorPalette.TURP -> R.drawable.ic_turp_mark
        ColorPalette.ARBOR -> R.drawable.ic_turp_mark_arbor
        ColorPalette.SYSTEM -> R.drawable.ic_turp_mark_system
        ColorPalette.GRAPHITE -> R.drawable.ic_turp_mark_graphite
        ColorPalette.OCEAN -> R.drawable.ic_turp_mark_ocean
        ColorPalette.VIOLET -> R.drawable.ic_turp_mark_violet
        ColorPalette.SUNSET -> R.drawable.ic_turp_mark_sunset
    }

@get:DrawableRes
internal val ColorPalette.launcherBackgroundDrawable: Int
    get() = when (this) {
        ColorPalette.TURP -> R.drawable.ic_turp_background
        ColorPalette.ARBOR -> R.drawable.ic_turp_background_arbor
        ColorPalette.SYSTEM -> R.drawable.ic_turp_background_system
        ColorPalette.GRAPHITE -> R.drawable.ic_turp_background_graphite
        ColorPalette.OCEAN -> R.drawable.ic_turp_background_ocean
        ColorPalette.VIOLET -> R.drawable.ic_turp_background_violet
        ColorPalette.SUNSET -> R.drawable.ic_turp_background_sunset
    }

@get:DrawableRes
internal val ColorPalette.launcherForegroundDrawable: Int
    get() = when (this) {
        ColorPalette.TURP -> R.drawable.ic_turp_foreground
        ColorPalette.ARBOR -> R.drawable.ic_turp_foreground_arbor
        ColorPalette.SYSTEM -> R.drawable.ic_turp_foreground_system
        ColorPalette.GRAPHITE -> R.drawable.ic_turp_foreground_graphite
        ColorPalette.OCEAN -> R.drawable.ic_turp_foreground_ocean
        ColorPalette.VIOLET -> R.drawable.ic_turp_foreground_violet
        ColorPalette.SUNSET -> R.drawable.ic_turp_foreground_sunset
    }
