package com.vhsnik.pethelper.ui.compose.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.theme.*

val preventions =
    listOf("Deworming", "Flea Prevention", "Tick Prevention")
@Composable
private fun ProgressBar(
    percentage: Float
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        CircularProgressIndicator(
            progress = { percentage.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxSize(),
            color = if (percentage == 1f) PastelleColors.GREEN.iconColor else PastelleColors.YELLOW.iconColor,
            trackColor = Color(0xFFD9D9D9),
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextMaker("${(percentage * 100).toInt()}%", 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            TextMaker(
                if (percentage == 1f) "Up to date" else "Needs action",
                10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatusProgBar(
    isVaccine: Boolean, percentage: Float,
    text: String,
    onClick: () -> Unit = {},
    isProtectedFleas: Boolean = false,
    isProtectedTicks: Boolean = false,
    isProtectedWorms: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier,
        colors = CardDefaults.cardColors(cardColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .wrapContentHeight()
        ) {
            TextMaker(
                if (isVaccine) "Vaccination Status" else "Protection Status",
                12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressBar(percentage)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.wrapContentHeight()) {
                    TextMaker(
                        if (percentage == 1f) "Great Job!" else "Needs Action",
                        12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker(text, 10.sp, fontWeight = FontWeight.Normal)
                    if (isVaccine) {
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick,
                            shape = RoundedCornerShape(5.dp),
                            content = {
                                TextMaker(
                                    "Learn More",
                                    10.sp,
                                    Color.White,
                                    FontWeight.SemiBold
                                )
                            },
                            colors = ButtonDefaults.buttonColors(buttonColor),
                            contentPadding = PaddingValues(8.dp, 4.dp),
                            interactionSource = null,
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
            }
            if (!isVaccine) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProtectionStatusProtected(
                        Modifier.weight(1f),
                        isProtectedFleas,
                        R.drawable.fleas_icon,
                        "Fleas"
                    )
                    ProtectionStatusProtected(
                        Modifier.weight(1f),
                        isProtectedTicks,
                        R.drawable.tick,
                        "Ticks"
                    )
                    ProtectionStatusProtected(
                        Modifier.weight(1f),
                        isProtectedWorms,
                        R.drawable.worm,
                        "Worms"
                    )
                }
            }
        }
    }
}

@Composable
private fun ProtectionStatusProtected(
    modifier: Modifier,
    isProtected: Boolean,
    icon: Int,
    label: String
) {
    Card(
        colors = CardDefaults.cardColors(if (isProtected) PastelleColors.GREEN.color else PastelleColors.YELLOW.color),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            1.dp,
            if (isProtected) PastelleColors.GREEN.borderColor else PastelleColors.YELLOW.borderColor
        ),
        modifier = modifier
    ) {
        Column(
            Modifier
                .padding(12.dp, 6.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), "$label Icon")
            Spacer(Modifier.height(2.dp))
            TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            TextMaker(
                if (isProtected) "Protected" else "Not Protected", 10.sp,
                if (isProtected) PastelleColors.GREEN.iconColor else PastelleColors.YELLOW.iconColor,
                FontWeight.SemiBold
            )

        }
    }
}

@Composable
fun HealthTip(
    color: Color,
    iconColor: Color,
    borderColor: Color,
    label: String,
    text: String,
    icon: Int
) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(color),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(painterResource(icon), "tips icon", tint = iconColor)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                TextMaker(label, 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                TextMaker(text, 12.sp, fontWeight = FontWeight.Normal)
            }
        }
    }
}