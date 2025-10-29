// ===== Theme.kt =====
package com.billybobbain.tasknow

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    PURPLE, BLUE, GREEN, ORANGE, PINK, TEAL
}

// Purple Theme
private val PurpleLightPrimary = Color(0xFF6200EE)
private val PurpleLightPrimaryContainer = Color(0xFFBB86FC)
private val PurpleLightSecondary = Color(0xFF03DAC6)
private val PurpleLightSecondaryContainer = Color(0xFFB2F5EA)

private val PurpleDarkPrimary = Color(0xFFBB86FC)
private val PurpleDarkPrimaryContainer = Color(0xFF6200EE)
private val PurpleDarkSecondary = Color(0xFF03DAC6)
private val PurpleDarkSecondaryContainer = Color(0xFF005048)

// Blue Theme
private val BlueLightPrimary = Color(0xFF2196F3)
private val BlueLightPrimaryContainer = Color(0xFFBBDEFB)
private val BlueLightSecondary = Color(0xFF03A9F4)
private val BlueLightSecondaryContainer = Color(0xFFB3E5FC)

private val BlueDarkPrimary = Color(0xFF64B5F6)
private val BlueDarkPrimaryContainer = Color(0xFF1976D2)
private val BlueDarkSecondary = Color(0xFF4FC3F7)
private val BlueDarkSecondaryContainer = Color(0xFF0277BD)

// Green Theme
private val GreenLightPrimary = Color(0xFF4CAF50)
private val GreenLightPrimaryContainer = Color(0xFFC8E6C9)
private val GreenLightSecondary = Color(0xFF8BC34A)
private val GreenLightSecondaryContainer = Color(0xFFDCEDC8)

private val GreenDarkPrimary = Color(0xFF81C784)
private val GreenDarkPrimaryContainer = Color(0xFF388E3C)
private val GreenDarkSecondary = Color(0xFFAED581)
private val GreenDarkSecondaryContainer = Color(0xFF558B2F)

// Orange Theme
private val OrangeLightPrimary = Color(0xFFFF9800)
private val OrangeLightPrimaryContainer = Color(0xFFFFE0B2)
private val OrangeLightSecondary = Color(0xFFFF5722)
private val OrangeLightSecondaryContainer = Color(0xFFFFCCBC)

private val OrangeDarkPrimary = Color(0xFFFFB74D)
private val OrangeDarkPrimaryContainer = Color(0xFFF57C00)
private val OrangeDarkSecondary = Color(0xFFFF8A65)
private val OrangeDarkSecondaryContainer = Color(0xFFD84315)

// Pink Theme
private val PinkLightPrimary = Color(0xFFE91E63)
private val PinkLightPrimaryContainer = Color(0xFFF8BBD0)
private val PinkLightSecondary = Color(0xFFFF4081)
private val PinkLightSecondaryContainer = Color(0xFFFF80AB)

private val PinkDarkPrimary = Color(0xFFF06292)
private val PinkDarkPrimaryContainer = Color(0xFFC2185B)
private val PinkDarkSecondary = Color(0xFFFF4081)
private val PinkDarkSecondaryContainer = Color(0xFFC51162)

// Teal Theme
private val TealLightPrimary = Color(0xFF009688)
private val TealLightPrimaryContainer = Color(0xFFB2DFDB)
private val TealLightSecondary = Color(0xFF00BCD4)
private val TealLightSecondaryContainer = Color(0xFFB2EBF2)

private val TealDarkPrimary = Color(0xFF4DB6AC)
private val TealDarkPrimaryContainer = Color(0xFF00796B)
private val TealDarkSecondary = Color(0xFF4DD0E1)
private val TealDarkSecondaryContainer = Color(0xFF00838F)

@Composable
fun getColorScheme(themeName: String, darkTheme: Boolean): ColorScheme {
    val theme = try {
        AppTheme.valueOf(themeName.uppercase())
    } catch (e: IllegalArgumentException) {
        AppTheme.PURPLE
    }

    return when (theme) {
        AppTheme.PURPLE -> if (darkTheme) {
            darkColorScheme(
                primary = PurpleDarkPrimary,
                primaryContainer = PurpleDarkPrimaryContainer,
                secondary = PurpleDarkSecondary,
                secondaryContainer = PurpleDarkSecondaryContainer,
                tertiary = Color(0xFFFFB74D),
                error = Color(0xFFCF6679)
            )
        } else {
            lightColorScheme(
                primary = PurpleLightPrimary,
                primaryContainer = PurpleLightPrimaryContainer,
                secondary = PurpleLightSecondary,
                secondaryContainer = PurpleLightSecondaryContainer,
                tertiary = Color(0xFFFF9800),
                error = Color(0xFFB00020)
            )
        }
        AppTheme.BLUE -> if (darkTheme) {
            darkColorScheme(
                primary = BlueDarkPrimary,
                primaryContainer = BlueDarkPrimaryContainer,
                secondary = BlueDarkSecondary,
                secondaryContainer = BlueDarkSecondaryContainer,
                tertiary = Color(0xFFFFB74D),
                error = Color(0xFFCF6679)
            )
        } else {
            lightColorScheme(
                primary = BlueLightPrimary,
                primaryContainer = BlueLightPrimaryContainer,
                secondary = BlueLightSecondary,
                secondaryContainer = BlueLightSecondaryContainer,
                tertiary = Color(0xFFFF9800),
                error = Color(0xFFB00020)
            )
        }
        AppTheme.GREEN -> if (darkTheme) {
            darkColorScheme(
                primary = GreenDarkPrimary,
                primaryContainer = GreenDarkPrimaryContainer,
                secondary = GreenDarkSecondary,
                secondaryContainer = GreenDarkSecondaryContainer,
                tertiary = Color(0xFFFFB74D),
                error = Color(0xFFCF6679)
            )
        } else {
            lightColorScheme(
                primary = GreenLightPrimary,
                primaryContainer = GreenLightPrimaryContainer,
                secondary = GreenLightSecondary,
                secondaryContainer = GreenLightSecondaryContainer,
                tertiary = Color(0xFFFF9800),
                error = Color(0xFFB00020)
            )
        }
        AppTheme.ORANGE -> if (darkTheme) {
            darkColorScheme(
                primary = OrangeDarkPrimary,
                primaryContainer = OrangeDarkPrimaryContainer,
                secondary = OrangeDarkSecondary,
                secondaryContainer = OrangeDarkSecondaryContainer,
                tertiary = Color(0xFFFFB74D),
                error = Color(0xFFCF6679)
            )
        } else {
            lightColorScheme(
                primary = OrangeLightPrimary,
                primaryContainer = OrangeLightPrimaryContainer,
                secondary = OrangeLightSecondary,
                secondaryContainer = OrangeLightSecondaryContainer,
                tertiary = Color(0xFFFF9800),
                error = Color(0xFFB00020)
            )
        }
        AppTheme.PINK -> if (darkTheme) {
            darkColorScheme(
                primary = PinkDarkPrimary,
                primaryContainer = PinkDarkPrimaryContainer,
                secondary = PinkDarkSecondary,
                secondaryContainer = PinkDarkSecondaryContainer,
                tertiary = Color(0xFFFFB74D),
                error = Color(0xFFCF6679)
            )
        } else {
            lightColorScheme(
                primary = PinkLightPrimary,
                primaryContainer = PinkLightPrimaryContainer,
                secondary = PinkLightSecondary,
                secondaryContainer = PinkLightSecondaryContainer,
                tertiary = Color(0xFFFF9800),
                error = Color(0xFFB00020)
            )
        }
        AppTheme.TEAL -> if (darkTheme) {
            darkColorScheme(
                primary = TealDarkPrimary,
                primaryContainer = TealDarkPrimaryContainer,
                secondary = TealDarkSecondary,
                secondaryContainer = TealDarkSecondaryContainer,
                tertiary = Color(0xFFFFB74D),
                error = Color(0xFFCF6679)
            )
        } else {
            lightColorScheme(
                primary = TealLightPrimary,
                primaryContainer = TealLightPrimaryContainer,
                secondary = TealLightSecondary,
                secondaryContainer = TealLightSecondaryContainer,
                tertiary = Color(0xFFFF9800),
                error = Color(0xFFB00020)
            )
        }
    }
}

@Composable
fun TaskNowTheme(
    themeName: String = "Purple",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(themeName, darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
