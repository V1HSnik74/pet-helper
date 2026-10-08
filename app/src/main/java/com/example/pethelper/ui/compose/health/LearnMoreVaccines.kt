package com.example.pethelper.ui.compose.health

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.example.pethelper.R
import com.example.pethelper.ui.compose.patterns.TextMaker
import com.example.pethelper.ui.theme.backgroundColor
import com.example.pethelper.ui.theme.cardColor
import com.example.pethelper.ui.theme.dividerColor
import com.example.pethelper.ui.theme.smallTextColor

private val dogVaccinesDesc =
    mapOf(
        "Rabies Vaccine" to "Protects against rabies, a fatal viral infection affecting the central nervous system, transmissible to humans and other animals",
        "DHPPi" to "Protects against major canine diseases: Distemper, Infectious Hepatitis, Parvovirus, and Parainfluenza",
        "Bordetella" to "Guards against Bordetella bronchiseptica, the primary cause of highly contagious kennel cough; recommended for dogs visiting parks, boarding, or grooming"
    )
private val catVaccinesDesc = mapOf(
    "Rabies Vaccine" to "Protects against rabies, a fatal viral infection affecting the central nervous system, transmissible to humans and other animals",
    "FVRCP" to "Core protection against three dangerous infections: Feline Viral Rhinotracheitis (herpesvirus), Calicivirus, and Panleukopenia (feline distemper)",
    "FeLV" to "Protects against feline leukemia virus, which impairs the immune system and can cause cancer; highly recommended for outdoor or social cats"
)

@Composable
fun LearnMore(animal: String, onDismiss: () -> Unit) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(backgroundColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        contentDescription = null,
                        painter = painterResource(R.drawable.vaccine_dialog),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker("Core Vaccines", 16.sp)
                    Spacer(Modifier.height(16.dp))
                    TextMaker(
                        "Core vaccines are recommended for all ${animal.lowercase()}s to protect them from serious and highly contagious diseases",
                        12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(Modifier.height(16.dp))
                    Card(
                        Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(cardColor),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            if (animal == "Dog") {
                                dogVaccinesDesc.entries.forEach {
                                    VaccineCard(it.key, it.value)
                                    if (it.key != dogVaccinesDesc.keys.last())
                                        HorizontalDivider(
                                            Modifier.fillMaxWidth(), 1.dp,
                                            dividerColor
                                        )
                                }
                            } else {
                                catVaccinesDesc.entries.forEach {
                                    VaccineCard(it.key, it.value)
                                    if (it.key != catVaccinesDesc.keys.last())
                                        HorizontalDivider(
                                            Modifier.fillMaxWidth(),
                                            1.dp,
                                            dividerColor
                                        )
                                }
                            }
                        }
                    }
                }
                Icon(
                    painterResource(R.drawable.cancel),
                    "Dismiss",
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 16.dp)
                        .clickable(indication = null, interactionSource = null) { onDismiss() }
                )
            }
        }
    }
}

@Composable
private fun VaccineCard(name: String, description: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painterResource(R.drawable.virus_icon), null)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            TextMaker(name, 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            TextMaker(description, 10.sp, smallTextColor, fontWeight = FontWeight.Medium)
        }
    }
}