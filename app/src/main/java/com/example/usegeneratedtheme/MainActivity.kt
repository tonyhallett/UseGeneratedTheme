package com.example.usegeneratedtheme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.usegeneratedtheme.ui.theme.Extended
import com.example.usegeneratedtheme.ui.theme.HarmonizedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HarmonizedTheme(isDynamic = false,content = {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    CustomColors()
                }
            })
        }
    }
}

@Composable
fun CustomColors() {
    val customColorRoles = Extended.color99
    Column{
        CustomColor("color", customColorRoles.color)
        CustomColor("onColor", customColorRoles.onColor)
        CustomColor("colorContainer", customColorRoles.colorContainer)
        CustomColor("onColorContainer", customColorRoles.onColorContainer)
    }
}

@Composable
fun CustomColor(role:String,color:Color){
    Text(text = "$role - ${toHex(color)}")

}

fun toHex(color:Color): String {
    return java.lang.String.format("#%06X", 0xFFFFFF and color.toArgb())
}