package com.vhsnik.pethelper.ui.compose.activity

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.vhsnik.pethelper.R
import com.vhsnik.pethelper.ui.compose.utils.*
import com.vhsnik.pethelper.ui.theme.buttonColor

private val icons = listOf(
    R.drawable.ball, R.drawable.frisbee, R.drawable.dog_toy, R.drawable.roll,
    R.drawable.cat_toy, R.drawable.warm_up, R.drawable.cool_down, R.drawable.cat_tree,
    R.drawable.mind_training, R.drawable.clicker, R.drawable.walking, R.drawable.swimming
)

private val units = listOf("minute(s)", "time(s)", "hour(s)")

@Composable
fun UpsertActivityDialog(
    onDismiss: () -> Unit,
    onSaveActivity: (activity: String, note: String, duration: String, icon: Int, petId: Int) -> Unit,
    isUpdate: Boolean,
    activity: String,
    note: String,
    duration: String,
    unit: String,
    icon: Int,
    petId: Int
) {
    var activity by remember { mutableStateOf(activity) }
    var note by remember { mutableStateOf(note) }
    var duration by remember { mutableStateOf(duration) }
    var unit by remember { mutableStateOf(unit) }
    var selectedIcon by remember { mutableIntStateOf(icon) }
    var isMenuOpened by remember { mutableStateOf(false) }
    DialogPattern(
        if (isUpdate) "Update Activity" else "Add Activity",
        R.drawable.addactivityicon,
        onDismiss,
        {
            onSaveActivity(
                activity,
                note,
                "$duration $unit",
                selectedIcon,
                petId
            )
        },
        activity.isNotBlank() && duration.isNotBlank()
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabelAndTextField(
                "Activity",
                activity,
                { activity = it },
                "e.g. Fetching"
            )
            LabelAndTextField(
                "Note (optional)",
                note,
                { note = it },
                "e.g. Heart training"
            )
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextMaker("Duration", 12.sp)
                Spacer(Modifier.width(8.dp))
                PortionSizeDurationValue(
                    duration,
                    { duration = it },
                    {
                        isMenuOpened = false
                        unit = it
                    },
                    isMenuOpened,
                    { isMenuOpened = false },
                    unit,
                    { isMenuOpened = true },
                    units
                )
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
                    icons.forEach {
                        Image(
                            painterResource(it),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = null
                                ) {
                                    selectedIcon = it
                                }
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (selectedIcon == it) buttonColor else Color.Transparent
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