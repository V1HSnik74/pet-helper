package com.vhsnik.pethelper.ui.compose.nutrition

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.*

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
    petId: Int,
    label: String,
    foodItem: String,
    note: String,
    portionSize: String,
    unit: String,
    icon: Int
) {
    var foodItem by remember { mutableStateOf(foodItem) }
    var note by remember { mutableStateOf(note) }
    var portionSize by remember { mutableStateOf(portionSize) }
    var measure by remember { mutableStateOf(unit) }
    var selectedIcon by remember { mutableIntStateOf(icon) }
    var isMenuOpened by remember { mutableStateOf(false) }
    DialogPattern(
        label,
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
            LabelAndTextField(
                "Food Item",
                foodItem,
                { foodItem = it },
                "e.g. Vegetables"
            )
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
                    TextMaker(
                        "Portion Size",
                        12.sp
                    )
                    Spacer(Modifier.width(8.dp))
                    PortionSizeDurationValue(
                        portionSize,
                        { portionSize = it },
                        {
                            isMenuOpened = false
                            measure = it
                        },
                        isMenuOpened,
                        { isMenuOpened = false },
                        measure,
                        { isMenuOpened = true },
                        measures)
                }
            }
            Column(Modifier.fillMaxWidth()) {
                TextMaker(
                    "Choose an Icon",
                    12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
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