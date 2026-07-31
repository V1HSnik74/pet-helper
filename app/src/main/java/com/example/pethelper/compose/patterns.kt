package com.example.pethelper.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.ui.theme.InterFamily

val buttonColor = Color(0xFFE27380)
val backgroundColor = Color(0xFFFFF8F2)
val backgroundScreenColor  = Color(0xFFFFF9F2)
val smallTextColor = Color(0xFFB0A8A3)
val selectedColor = Color(0xFFFDF1ED)
val cardColor = Color(0xFFFFFCF9)
val brownColor = Color(0xFFAF8268)
@Composable
fun TextMaker(text: String, fontSize: TextUnit,
              color: Color = Color.Black,
              fontWeight: FontWeight = FontWeight.Bold, modifier: Modifier = Modifier,
              textDecoration: TextDecoration = TextDecoration.None
){
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = InterFamily,
        color = color,
        fontWeight = fontWeight,
        modifier = modifier,
        textDecoration = textDecoration
    )
}

@Composable
fun ButtonMaker(text: String, onClick: () -> Unit, enabled: Boolean = true){
    Button(onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = RoundedCornerShape(25.dp))
    {
        TextMaker(text, 14.sp, Color.White)
    }
}