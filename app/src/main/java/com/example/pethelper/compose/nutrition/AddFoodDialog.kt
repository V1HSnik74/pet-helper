package com.example.pethelper.compose.nutrition

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.pethelper.R
import com.example.pethelper.compose.patterns.DialogPattern
import com.example.pethelper.compose.patterns.LabelAndTextField
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.buttonColor
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.compose.patterns.smallTextColor
import com.example.pethelper.compose.petScreens.textFieldCursorColor
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
    var selectedIcon by remember { mutableIntStateOf(R.drawable.dry_food) }
    var isMenuOpened by remember { mutableStateOf(false) }
    DialogPattern(
        "Add Food Item",
        R.drawable.add_food_icon,
        onDismiss,
        {
            onSaveFoodItem(
                foodItem,
                note,
                portionSize,
                selectedIcon,
                measure,
                petId
            )
        },
        foodItem.isNotBlank() && portionSize.isNotBlank()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabelAndTextField("Food Item", foodItem, { foodItem = it }, "e.g. Vegetables")
            LabelAndTextField(
                "Description (optional)",
                note,
                { note = it },
                "e.g. Zucchini, pumpkin"
            )
            Column(Modifier.fillMaxWidth()) {
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
            }
            Column(Modifier.fillMaxWidth()) {
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
                    .height(30.dp),
                textStyle = TextStyle(
                    fontSize = 12.sp, fontFamily = InterFamily, textAlign = TextAlign.Center
                ),
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