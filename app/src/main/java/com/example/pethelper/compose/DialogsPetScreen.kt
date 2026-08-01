package com.example.pethelper.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R

@Composable
fun UpdateGenderDialog(onDismiss: () -> Unit,
                       onUpdateGender: (gender: String) -> Unit,
                       currGender: String){
    var gender by remember(currGender) {mutableStateOf(currGender)}
    Dialog(
        onDismiss,
        DialogProperties(usePlatformDefaultWidth = false)
    ){
        Card(
            Modifier
                .wrapContentHeight()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(backgroundColor)
        ) {
            Box(Modifier.fillMaxWidth()){
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(alignment = Alignment.TopCenter,
                        painter = painterResource(R.drawable.gender_icon),
                        contentDescription = "gender icon")
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Edit Gender", 20.sp)
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth()) {
                        GenderCard("Male", gender, {gender = "Male"},
                            Modifier.weight(1f))
                        Spacer(Modifier.width(20.dp))
                        GenderCard("Female", gender, {gender = "Female"},
                            Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(24.dp))
                    ButtonMaker("Save Gender",
                        onClick = { onUpdateGender(gender) }
                    )
                }
                IconButton(onDismiss,
                     modifier = Modifier.align(Alignment.TopEnd).padding(end = 8.dp, top = 8.dp)) {
                    Image(
                        painterResource(R.drawable.cancel),
                        contentDescription = "cancel"
                    )
                }
            }

        }
    }

}

@Composable
fun UpdateSmallDialog(onDismiss: () -> Unit, onUpdateInfo: (value: String) -> Unit, painter: Painter,
                      label: String, placeholder: String,
                      onValueChange: (String) -> Unit,
                      value: String,
                      errorMessage: String? = null,
                      isMicrochip: Boolean = false) {
    val digitsOnly = value.filter { it.isDigit() }
    val isValid = if (isMicrochip) (digitsOnly.length == 15 || value.isEmpty()) else value.isNotBlank()
    val showError = (errorMessage != null) && value.isNotBlank() && !isValid
    Dialog(
        onDismiss,
        DialogProperties(usePlatformDefaultWidth = false)
    )
    {
        Card(
            Modifier
                .wrapContentHeight().fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(backgroundColor)
        )
        {
            Box(
                Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 42.dp, start = 42.dp, top = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        alignment = Alignment.TopCenter,
                        painter = painter,
                        contentDescription = "$label icon"
                    )
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Edit $label", 20.sp)
                    Spacer(Modifier.height(24.dp))
                    TextMaker(label, 14.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(value = value, onValueChange = {
                        if (isMicrochip) onValueChange(it.filter {it.isDigit()}.take(15))
                        else onValueChange(it)
                    },
                        placeholder = {
                            TextMaker(placeholder, 12.sp, smallTextColor)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldContainerColor,
                            unfocusedContainerColor = textFieldContainerColor,
                            focusedBorderColor = textFieldCursorColor,
                            unfocusedBorderColor = textFieldCursorColor,
                            cursorColor = textFieldCursorColor
                        ),
                        isError = showError,
                        supportingText =  if (showError){
                            {TextMaker(errorMessage, 12.sp, Color.Red)}
                            } else null

                    )
                    Spacer(Modifier.height(24.dp))
                    ButtonMaker("Save $label", {
                        val finalValue = if (isMicrochip){
                            if (value.isEmpty()) "No microchip ID"
                            else (value.chunked(3).joinToString(" "))
                        } else value
                        onUpdateInfo(finalValue)}, enabled = isValid)
                    Spacer(Modifier.height(28.dp))
                }
                IconButton(onDismiss, modifier = Modifier.align(Alignment.TopEnd).padding(end = 8.dp, top = 8.dp)) {
                    Image(
                        painterResource(R.drawable.cancel),
                        contentDescription = "cancel"
                    )
                }
            }
        }
    }
}

@Composable
fun GenderCard(cardGender: String, selectedGender: String, onClick: () -> Unit,
               modifier: Modifier){
    val isSelected = cardGender == selectedGender
    val interactionSource = remember { MutableInteractionSource() }
    Card(modifier
        .height(110.dp)
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) { onClick() },
        colors = CardDefaults.cardColors(
         if (isSelected) selectedColor else cardColor),
        border = BorderStroke(width = 1.dp,
            color = if (isSelected) buttonColor else Color.Transparent),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(10.dp)
    ){
        Column(Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Icon(painterResource(if (cardGender == "Male")
                R.drawable.male_not_selected else
                R.drawable.female_dialog),
                tint = if (isSelected) buttonColor else brownColor,
                contentDescription = "$cardGender icon")

            Spacer(Modifier.height(8.dp))
            TextMaker(if (cardGender == "Male") "Male" else "Female",
            14.sp)
        }
    }
}