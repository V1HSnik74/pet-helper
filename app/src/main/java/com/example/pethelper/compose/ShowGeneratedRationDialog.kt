package com.example.pethelper.compose


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R
import com.example.pethelper.db.FoodItem

@Composable
fun ShowGeneratedRationDialog(
    onDismiss: () -> Unit,
    onAddFoodItems: () -> Unit,
    generatedItems: List<FoodItem>
) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(cardColor),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextMaker(
                        "Your pet's ration is ready!",
                        16.sp,
                        modifier = Modifier.align(Alignment.Start),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))
                    generatedItems.forEachIndexed { index, item ->
                        FoodItemCard(item, index < generatedItems.lastIndex)
                    }
                    Spacer(Modifier.height(16.dp))
                    ButtonMaker(
                        "Save Ration",
                        onAddFoodItems
                    )
                }
            }
        }
    }
}

@Composable
private fun FoodItemCard(foodItem: FoodItem, isLine: Boolean = true) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .padding(12.dp, 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(painterResource(foodItem.icon), contentDescription = "food item icon")
                Spacer(Modifier.width(10.dp))
                Column(
                    Modifier.wrapContentSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    TextMaker(foodItem.name, 12.sp, fontWeight = FontWeight.SemiBold)
                    if (foodItem.description.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        TextMaker(foodItem.description, 10.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }
            Row(
                Modifier.wrapContentSize(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                PortionSizeCard(foodItem.portionSize, foodItem.unit)
                Spacer(Modifier.width(10.dp))
                Icon(
                    painterResource(R.drawable.pen_notes),
                    tint = Color(0xFFAF8268),
                    modifier = Modifier.size(18.dp),
                    contentDescription = "edit food item"
                )
            }
        }
        if (isLine) {
            HorizontalDivider(
                Modifier.fillMaxWidth(),
                1.dp,
                Color(0xFFF2F2F2)
            )
        }
    }
}

@Composable
private fun PortionSizeCard(portionSize: String, unit: String) {
    Card(
        Modifier.wrapContentSize(),
        colors = CardDefaults.cardColors(cardColor),
        border = BorderStroke(1.dp, Color(0xFFAF8268)),
        shape = RoundedCornerShape(5.dp)
    ) {
        TextMaker(
            "$portionSize $unit", 10.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp, 2.dp)
        )
    }
}