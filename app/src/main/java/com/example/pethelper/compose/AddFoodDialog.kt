package com.example.pethelper.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R
import com.example.pethelper.ui.theme.InterFamily

private val measures = listOf("g", "cup", "oz", "kg", "lb")

private enum class FoodItems(val foodItem: String, val icon: Int) {
    DRY_FOOD("Dry Food", R.drawable.dry_food),
    WET_FOOD("Wet Food", R.drawable.wet_food),
    BEEF("Beef Meat", R.drawable.meat),
    LIVER("Liver", R.drawable.liver),
    BONE("Duck Throats", R.drawable.bone),
    FISH("Fish", R.drawable.fish),
    CHICKEN("Chicken Meat", R.drawable.chicken),
    FRUITS("Fruits", R.drawable.fruits),
    VEGETABLES("Vegetables", R.drawable.vegetables),
    BERRIES("Berries", R.drawable.berries),
    EGGS("Eggs", R.drawable.egg),
    DAIRY("Dairy", R.drawable.diary),
    WATER("Water", R.drawable.water)
}

@Composable
fun AddFoodDialog(
    onDismiss: () -> Unit,
    onSaveFoodItem: (item: String, note: String, portionSize: String, icon: Int, unit: String, petId: Int) -> Unit,
    petId: Int
) {
    var foodItem by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var portionSize by remember { mutableStateOf("") }
    var measure by remember { mutableStateOf("g") }
    var selectedIcon by remember { mutableStateOf<Int?>(null) }
    var isMenuOpened by remember { mutableStateOf(false) }
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
                        contentDescription = "Food Item icon",
                        painter = painterResource(R.drawable.add_food_icon)
                    )
                    Spacer(Modifier.height(8.dp))
                    TextMaker("Add Food Item", 16.sp)
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Food Item", 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
                    BasicTextFieldMaker(
                        foodItem, { foodItem = it }, "e.g. Vegetables", Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.Start) {
                        TextMaker("Description", 12.sp)
                        TextMaker("(optional)", 12.sp, smallTextColor)
                    }
                    Spacer(Modifier.height(8.dp))
                    BasicTextFieldMaker(
                        note, { note = it }, "e.g. Zucchini, pumpkin", Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextMaker("Portion Size", 12.sp)
                        Spacer(Modifier.width(8.dp))
                        PortionSizeValue(portionSize, { portionSize = it }, {
                            isMenuOpened = false
                            measure = it
                        }, isMenuOpened, { isMenuOpened = false }, measure, { isMenuOpened = true })
                    }
                    Spacer(Modifier.height(16.dp))
                    TextMaker("Choose an Icon", 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FoodItems.entries.forEach {
                            Image(
                                painterResource(it.icon),
                                contentDescription = "${it.foodItem} icon",
                                modifier = Modifier
                                    .clickable(
                                        indication = null,
                                        interactionSource = null
                                    ) {
                                        selectedIcon = it.icon
                                    }
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (selectedIcon == it.icon) buttonColor else Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    ButtonMaker(
                        "Save Food Item",
                        {
                            onSaveFoodItem(
                                foodItem,
                                note,
                                portionSize,
                                selectedIcon!!,
                                measure,
                                petId
                            )
                        },
                        enabled = foodItem.isNotEmpty() && portionSize.isNotEmpty() && selectedIcon != null
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

@Composable
fun PortionSizeValue(
    value: String,
    onValueChange: (String) -> Unit,
    onClick: (String) -> Unit,
    isMenuOpened: Boolean,
    onDismiss: () -> Unit,
    measure: String,
    onClickArrow: () -> Unit
) {
    Card(
        Modifier.wrapContentSize(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, textFieldCursorColor),
        colors = CardDefaults.cardColors(cardColor)
    ) {
        Row(
            Modifier
                .wrapContentWidth()
                .padding(8.dp, 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.filter { char -> char.isDigit() }) },
                singleLine = true,
                modifier = Modifier
                    .width(30.dp)
                    .height(40.dp),
                textStyle = TextStyle(
                    fontSize = 12.sp, fontFamily = InterFamily, textAlign = TextAlign.Center),
                cursorBrush = SolidColor(textFieldCursorColor),
                decorationBox = {
                    Box(
                        modifier = Modifier
                            .background(
                                cardColor
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (value.isEmpty()) {
                            TextMaker(
                                "20", 12.sp, smallTextColor,
                                FontWeight.Normal
                            )
                        }
                        it.invoke()
                    }
                }
            )
            Spacer(Modifier.width(4.dp))
            TextMaker(measure, 10.sp)
            DropdownMenu(
                expanded = isMenuOpened,
                onDismissRequest = onDismiss,
                modifier = Modifier
                    .heightIn(max = 100.dp)
                    .background(Color.White)
            ) {
                measures.forEach {
                    DropdownMenuItem(
                        { TextMaker(it, 12.sp) },
                        { onClick(it) }
                    )
                }
            }
            Image(
                painterResource(R.drawable.small_arrow),
                "list of units",
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = null
                ) { onClickArrow() }
            )
        }
    }
}