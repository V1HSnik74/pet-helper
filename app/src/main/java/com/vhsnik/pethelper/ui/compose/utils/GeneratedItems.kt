package com.vhsnik.pethelper.ui.compose.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.theme.*

@Composable
fun <T> GeneratedActivityNutritionPlan(
    onDismiss: () -> Unit,
    generatedItems: List<T>,
    onSaveActivities: () -> Unit,
    onDeleteActivities: () -> Unit,
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
                                dividerColor
                            )
                        }
                    }
                }
                Column(Modifier.fillMaxWidth()) {
                    ButtonMaker("Save $label Plan", onSaveActivities)
                    Spacer(Modifier.height(4.dp))
                    ButtonMaker("Delete saved plan and Add a new one", {
                        onDeleteActivities()
                        onSaveActivities()
                    })
                }
            }
        }
    }
}

@Composable
fun GeneratedItem(
    name: String,
    note: String,
    icon: Int,
    additionalInfo: String,
    onDeleteItem: () -> Unit = {},
    onEditItem: () -> Unit = {},
    isGeneratedList: Boolean = false
) {
    var isMoreOptionsOpened by remember { mutableStateOf(false) }
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
            Row(
                Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextMaker(additionalInfo, 10.sp, fontWeight = FontWeight.SemiBold)
                if (!isGeneratedList) {
                    Spacer(Modifier.width(4.dp))
                    Box {
                        Icon(
                            Icons.Default.MoreVert, "More options", Modifier
                                .size(18.dp)
                                .clickable()
                                { isMoreOptionsOpened = true })
                        DropdownMoreOptions(
                            isMoreOptionsOpened,
                            { isMoreOptionsOpened = false },
                            onEditItem,
                            onDeleteItem
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun GenerateButton(onClick: () -> Unit, label: String, isGenerating: Boolean) {
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