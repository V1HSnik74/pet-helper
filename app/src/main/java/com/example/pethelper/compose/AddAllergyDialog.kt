package com.example.pethelper.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R

private enum class PopularAllergies(val allergy: String) {
    CHICKEN("Chicken"),
    BEEF("Beef"),
    DAIRY("Dairy"),
    EGGS("Eggs"),
    FISH("Fish"),
    WHEAT("Wheat"),
    CORN("Corn"),
    SOY("Soy")
}

@Composable
fun AddAllergyDialog(
    onDismiss: () -> Unit, onUpdateAllergy: (name: String, petId: Int) -> Unit,
    petId: Int
) {
    var allergy by remember { mutableStateOf("") }
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
                        contentDescription = "Allergy icon",
                        painter = painterResource(R.drawable.allergy_icon)
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker("Add Allergy/Sensitive", 16.sp)
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Allergy", 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
                    BasicTextFieldMaker(
                        allergy,
                        { allergy = it },
                        "e.g. Fish", Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    TextMaker(
                        "Popular Allergies",
                        12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PopularAllergies.entries.forEach {
                            DialogChip(it.allergy == allergy, it.allergy) {
                                allergy = it.allergy
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    ButtonMaker(
                        "Save Allergy/Sensitive",
                        { onUpdateAllergy(allergy, petId) },
                        enabled = allergy.isNotEmpty()
                    )
                }
                IconButton(
                    onDismiss, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 20.dp, top = 16.dp)
                ) {
                    Image(
                        painterResource(R.drawable.cancel),
                        contentDescription = "cancel"
                    )
                }
            }
        }
    }
}