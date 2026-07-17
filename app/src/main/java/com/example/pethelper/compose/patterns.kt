package com.example.pethelper.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.example.pethelper.ui.theme.InterFamily

val buttonColor = Color(0xFFE27380)
val backgroundColor = Color(0xFFFFF8F2)
val backgroundScreenColor  = Color(0xFFFFF9F2)
val smallTextColor = Color(0xFFB0A8A3)
val selectedColor = Color(0xFFFDF1ED)
val cardColor = Color(0xFFFFFCF9)
@Composable
fun TextMaker(text: String, fontSize: TextUnit,
              color: Color = Color.Black,
              fontWeight: FontWeight = FontWeight.Bold, modifier: Modifier = Modifier){
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = InterFamily,
        color = color,
        fontWeight = fontWeight,
        modifier = modifier
    )
}