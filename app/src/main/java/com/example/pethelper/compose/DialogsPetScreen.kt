package com.example.pethelper.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState

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
                     modifier = Modifier
                         .align(Alignment.TopEnd)
                         .padding(end = 8.dp, top = 8.dp)) {
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
                .wrapContentHeight()
                .fillMaxWidth(0.9f),
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
                IconButton(onDismiss, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 8.dp)) {
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
fun UpdateColorDialog(color: String, onDismiss: () -> Unit, onUpdateColor: (String) -> Unit){
    var selectedColor by remember {mutableStateOf(color)}
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)){
        Card(colors = CardDefaults.cardColors(backgroundColor),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(0.9f))
        {
            Box(Modifier.fillMaxWidth()){
                Column(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp, horizontal = 42.dp),
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Image(
                        alignment = Alignment.TopCenter,
                        painter = painterResource(R.drawable.color_icon),
                        contentDescription = "color icon"
                    )
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Edit Color", 20.sp)
                    Spacer(Modifier.height(24.dp))
                    OutlinedTextField(value = selectedColor, { selectedColor = it },
                        placeholder = {TextMaker("e.g. Black", 14.sp,
                            fontWeight = FontWeight.Normal)},
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = textFieldContainerColor,
                            unfocusedContainerColor = textFieldContainerColor,
                            focusedBorderColor = textFieldCursorColor,
                            unfocusedBorderColor = textFieldCursorColor,
                            cursorColor = textFieldCursorColor))
                    Spacer(Modifier.height(20.dp))
                    TextMaker("Popular colors", 12.sp, Color(0xFF393939), FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween){
                        ColorCircle(Colors.CREME.values,
                            selectedColor.equals(Colors.CREME.values, ignoreCase = true),
                            { selectedColor = Colors.CREME.values},
                            Colors.CREME.color)
                        ColorCircle(Colors.GOLDEN.values,
                            selectedColor.equals(Colors.GOLDEN.values, ignoreCase = true),
                            {selectedColor = Colors.GOLDEN.values},
                            Colors.GOLDEN.color)
                        ColorCircle(Colors.BROWN.values,
                            selectedColor.equals(Colors.BROWN.values, ignoreCase = true),
                            {selectedColor = Colors.BROWN.values},
                            Colors.BROWN.color)
                        ColorCircle(Colors.BLACK.values,
                            selectedColor.equals(Colors.BLACK.values, ignoreCase = true),
                            {selectedColor = Colors.BLACK.values},
                            Colors.BLACK.color)
                        ColorCircle(Colors.WHITE.values,
                            selectedColor.equals(Colors.WHITE.values, ignoreCase = true),
                            {selectedColor = Colors.WHITE.values},
                            Colors.WHITE.color)
                        ColorCircle(Colors.GRAY.values,
                            selectedColor.equals(Colors.GRAY.values, ignoreCase = true),
                            {selectedColor = Colors.GRAY.values},
                            Colors.GRAY.color)
                    }
                    Spacer(Modifier.height(32.dp))
                    ButtonMaker("Save Color", onClick = {onUpdateColor(selectedColor)},
                        enabled = selectedColor.isNotEmpty())
                }
                IconButton(onDismiss, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 8.dp)) {
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
fun UpdateNeutered(onDismiss: () -> Unit, onUpdateNeutered: (String) -> Unit, gender: String, isNeutered: String){
    var selectedIsNeutered by remember { mutableStateOf(isNeutered) }
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            colors = CardDefaults.cardColors(backgroundColor),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(0.9f)
        ) {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        alignment = Alignment.TopCenter,
                        painter = painterResource(R.drawable.neutured_icon),
                        contentDescription = "neutered icon"
                    )
                    Spacer(Modifier.height(16.dp))
                    TextMaker(if (gender == "Male") "Edit Neutered" else "Edit Spayed", 20.sp)
                    Spacer(Modifier.height(24.dp))
                    NeuteredCard(
                        selectedIsNeutered == "Yes",
                        onClick = { selectedIsNeutered = "Yes" },
                        "Yes")
                    Spacer(Modifier.height(8.dp))
                    NeuteredCard(
                        selectedIsNeutered == "No",
                        onClick = { selectedIsNeutered = "No" },
                        "No")
                    Spacer(Modifier.height(8.dp))
                    NeuteredCard(
                        selectedIsNeutered == "Not sure",
                        onClick = { selectedIsNeutered = "Not sure" },
                        "Not sure")
                    Spacer(Modifier.height(24.dp))
                    ButtonMaker("Save", {onUpdateNeutered(selectedIsNeutered)},
                        enabled = selectedIsNeutered.isNotEmpty())
                }
                IconButton(onDismiss, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 8.dp)) {
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
fun UpdateWeightHeightDialog(onDismiss: () -> Unit, onUpdateValue: (Float) -> Unit, isWeight: Boolean, valueInt: Int,
                             valueFloat: Int, count: Int, firstValue: Int, lastValue: Int){
    var selectedInteger by remember {mutableIntStateOf(valueInt)}
    var selectedFraction by remember {mutableIntStateOf(valueFloat)}
    val totalValue = selectedInteger + (selectedFraction / 10f)
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            colors = CardDefaults.cardColors(backgroundColor),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(0.9f)
        ) {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        alignment = Alignment.TopCenter,
                        painter = if (isWeight) painterResource(R.drawable.weight_icon_dialog)
                        else painterResource(R.drawable.height_icon_dialog),
                        contentDescription = "neutered icon"
                    )
                    Spacer(Modifier.height(16.dp))
                    TextMaker(if (isWeight) "Edit Weight" else "Edit Height", 20.sp)
                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center){
                        TextMaker("$selectedInteger.$selectedFraction",
                            32.sp, buttonColor, FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        TextMaker(if (isWeight) "kg" else "cm", 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically){
                        Column(Modifier.wrapContentWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            TextMaker(if (isWeight) "kg" else "cm",
                                14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(10.dp))
                            WheelPickerCard(count, selectedInteger, {selectedInteger = it}, firstValue)
                            Spacer(Modifier.height(10.dp))
                            TextMaker("$firstValue-$lastValue", 14.sp, smallTextColor,
                                FontWeight.SemiBold)
                        }
                        Image(painterResource(R.drawable.dot), contentDescription = "number splitter")
                        Column(Modifier.wrapContentWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center) {
                            TextMaker(if (isWeight) ".kg" else ".cm",
                                14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(10.dp))
                            WheelPickerCard(10, selectedFraction, {selectedFraction = it}, 0)
                            Spacer(Modifier.height(10.dp))
                            TextMaker("0-9", 14.sp, smallTextColor,
                                FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    ButtonMaker(if (isWeight) "Save Weight" else "Save Height",
                        onClick = {onUpdateValue(totalValue)},
                        enabled = totalValue != 0f)
                }
                IconButton(onDismiss, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 8.dp)) {
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
fun WheelPickerCard(count: Int, initIndex: Int, onValueChange: (Int) -> Unit, firstValue: Int){
    val state = rememberFWheelPickerState(initIndex - firstValue)
    LaunchedEffect(state.currentIndex) {
        onValueChange(state.currentIndex + firstValue)
    }
    Card(shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFFFD8C1)),
        colors = CardDefaults.cardColors(backgroundColor),
        modifier = Modifier.width(100.dp))
    {
        Box(Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center){
            Box(Modifier.fillMaxWidth().height(40.dp).background(selectedColor, RoundedCornerShape(10.dp)))
            FVerticalWheelPicker(count = count,
                unfocusedCount = 1,
                itemHeight = 40.dp,
                state = state) { index ->
                val displayValue = index + firstValue
                val isSelected = state.currentIndexSnapshot == index
                Box(Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center){
                    TextMaker(displayValue.toString(), 24.sp,
                        if (isSelected) buttonColor else Color.Black,
                        FontWeight.Medium)
                }
            }
        }

    }
}

@Composable
fun NeuteredCard(isSelected: Boolean, onClick: () -> Unit, text: String){
    Card(Modifier.fillMaxWidth().clickable(interactionSource = null,
        indication = null){onClick()},
        colors = CardDefaults.cardColors(if (isSelected) selectedColor else cardColor),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            color = if (isSelected) buttonColor else Color(0xFFAF8268),
            width = 1.dp
        )){
        Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Image(if (isSelected) painterResource(R.drawable.neutered_selected)
            else painterResource(R.drawable.neutered_not_selected),
                contentDescription = if (isSelected) "Selected" else "Not selected"
            )
            Spacer(Modifier.width(10.dp))
            TextMaker(text, 14.sp, fontWeight = FontWeight.SemiBold)
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

@Composable
fun ColorCircle(value: String, isSelected: Boolean, onClick: () -> Unit, color: Color){
    Box(Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(color)
        .border(
            color = if (isSelected) buttonColor else {
                if (value == "White") Color.Black else Color.Transparent
            },
            width = if (isSelected) 2.dp else 0.5.dp,
            shape = CircleShape
        )
        .clickable(onClick = onClick)
    )
}

enum class Colors(val color: Color, val values: String){
    BLACK(Color.Black, "Black"),
    CREME(Color(0xFFFFEAC0),"Creme"),
    GOLDEN(Color(0xFFEFB644),"Golden"),
    BROWN(Color(0xFFA14E00), "Brown"),
    WHITE(Color.White, "White"),
    GRAY(Color.Gray, "Gray")
}