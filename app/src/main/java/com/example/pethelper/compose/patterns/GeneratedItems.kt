package com.example.pethelper.compose.patterns

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

@Composable
fun <T> GeneratedActivityNutritionPlan(
    onDismiss: () -> Unit,
    generatedItems: List<T>,
    onSaveActivities: () -> Unit,
    label: String,
    itemCard: @Composable (item: T) -> Unit
) {
    Dialog(onDismiss, DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(cardColor)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextMaker(
                    "Your $label plan is ready!",
                    14.sp, fontWeight = FontWeight.SemiBold
                )
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                ) {
                    itemsIndexed(generatedItems) { index, it ->
                        itemCard(it)
                        if (generatedItems.lastIndex != index) {
                            HorizontalDivider(
                                Modifier.fillMaxWidth(),
                                1.dp,
                                Color(0xFFF2F2F2)
                            )
                        }
                    }
                }
                ButtonMaker("Save $label Plan", onSaveActivities)
            }
        }
    }
}

@Composable
fun GeneratedItem(name: String, note: String, icon: Int, additionalInfo: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(painterResource(icon), null, Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                TextMaker(name, 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                TextMaker(note, 10.sp, smallTextColor, FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier.wrapContentWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextMaker(additionalInfo, 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun GenerateButton(onClick:() -> Unit, label: String, isGenerating: Boolean){
    Button(
        onClick,
        Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp, 10.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(buttonColor),
        interactionSource = null,
        enabled = !isGenerating
    ) {
        Row(
            Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painterResource(R.drawable.ai_icon),
                null,
                tint = Color.White
            )
            TextMaker(
                if (!isGenerating) "Generate $label Plan" else "Generating...",
                12.sp,
                Color.White,
                FontWeight.SemiBold
            )
        }
    }
}