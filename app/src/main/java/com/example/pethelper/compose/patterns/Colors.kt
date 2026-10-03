package com.example.pethelper.compose.patterns

import androidx.compose.ui.graphics.Color

enum class PastelleColors(
    val color: Color,
    val iconColor: Color,
    val borderColor: Color,
    val buttonColor: Color
) {
    PINK(Color(0xFFFDF1ED), Color(0xFFE27380), Color(0xFFFFE3E7), Color(0xFFFBE4E7)),
    PURPLE(Color(0xFFF8F2F9), Color(0xFF554184), Color(0xFFEAE2FF), Color(0xFFE4DCF7)),
    GREEN(Color(0xFFEFF6E9), Color(0xFF008400), Color(0xFFDFEEDF), Color(0xFFD3F3D3)),
    YELLOW(Color(0xFFFFF6E8), Color(0xFFD27918), Color(0xFFFDF1ED), Color(0xFFFEE8D1)),
    BLUE(Color(0xFFF3F6FA), Color(0xFF265896), Color(0xFFD2E2F6), Color(0xFFD5E8FF))
}