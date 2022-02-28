package com.example.usegeneratedtheme.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.color.MaterialColors
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

fun getGetColorRole(): KFunction<*> {
    val materialColorsClass = MaterialColors::class
    val members = materialColorsClass.declaredFunctions
    val reflectGetColorRole = members.first { it.name == "getColorRole" }
    reflectGetColorRole.apply { isAccessible = true }
    return reflectGetColorRole
}

val reflectGetColorRole = getGetColorRole();

private val LightThemeColors = lightColorScheme(

    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
)
private val DarkThemeColors = darkColorScheme(

    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
)


data class CustomColor(val name:String, val color:Color, val harmonized: Boolean, var roles:CorrectColorRoles)
data class ExtendedColors(val colors: Array<CustomColor>)


fun setupErrorColors(colorScheme: ColorScheme, isLight: Boolean): ColorScheme {
    val harmonizedError =
        MaterialColors.harmonize(error.toArgb(), colorScheme.primary.toArgb())
    val roles = MaterialColors.getColorRoles(harmonizedError, isLight)
    //returns a colorScheme with newly harmonized error colors
    return colorScheme.copy(
        error = Color(roles.accent),
        onError = Color(roles.onAccent),
        errorContainer = Color(roles.accentContainer),
        onErrorContainer = Color(roles.onAccentContainer)
    )
}
val initializeExtended = ExtendedColors(
    arrayOf(
        CustomColor("color99", color99, color99Harmonize, CorrectColorRoles(true, Color.Black)),
    ))

data class ColorRoleTones(val color:Int, val onColor:Int, val colorContainer: Int, val onColorContainer: Int)



class CorrectColorRoles(isLight: Boolean,paletteColor: Color){
    private val lightColorRoleTones : ColorRoleTones = ColorRoleTones(40,100,90,10)
    private val darkColorRoleTones : ColorRoleTones = ColorRoleTones(80,20,30,90)
    private val paletteColor : Int = paletteColor.toArgb()
    var color:Color
    var onColor:Color
    var colorContainer:Color
    var onColorContainer:Color


    init {
        val tones = if(isLight) lightColorRoleTones else darkColorRoleTones
        this.color = getColorRole(tones.color)
        this.onColor = getColorRole(tones.onColor)
        this.colorContainer = getColorRole(tones.colorContainer)
        this.onColorContainer = getColorRole(tones.onColorContainer)
    }

    private fun getColorRole(tone:Int):Color{
        val color = reflectGetColorRole.call(paletteColor, tone)
        return Color(color as Int)
    }
}

fun setupCustomColors(
    colorScheme: ColorScheme,
    isLight: Boolean
): ExtendedColors {
    initializeExtended.colors.forEach {customColor ->
        // Retrieve record
        val shouldHarmonize = customColor.harmonized
        // Blend or not
        if (shouldHarmonize) {
            val blendedColor =
                MaterialColors.harmonize(customColor.color.toArgb(), colorScheme.primary.toArgb())
            customColor.roles = CorrectColorRoles(isLight,Color(blendedColor))
        } else {
            customColor.roles = CorrectColorRoles(isLight,customColor.color)
        }
    }
    return initializeExtended
}

val LocalExtendedColors = staticCompositionLocalOf {
    initializeExtended
}


@Composable
fun HarmonizedTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamic: Boolean = true,
    content: @Composable() () -> Unit
) {
    val canDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colors = if (isDynamic and canDynamicColor) {
        val context = LocalContext.current
        if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (useDarkTheme) DarkThemeColors else LightThemeColors
    }
    // I added
    val errorHarmonize = true
    val colorsWithHarmonizedError = if(errorHarmonize) setupErrorColors(colors, !useDarkTheme) else colors

    val extendedColors = setupCustomColors(colors, !useDarkTheme)
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorsWithHarmonizedError,
            typography = AppTypography,
            content = content
        )
    }
}

object Extended {
    val color99: CorrectColorRoles
        @Composable
        get() = LocalExtendedColors.current.colors.get(0).roles
}