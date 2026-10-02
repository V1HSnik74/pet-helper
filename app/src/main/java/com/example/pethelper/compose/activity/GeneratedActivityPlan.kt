package com.example.pethelper.compose.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.pethelper.compose.patterns.ButtonMaker
import com.example.pethelper.compose.patterns.TextMaker
import com.example.pethelper.compose.patterns.cardColor
import com.example.pethelper.compose.patterns.smallTextColor
import com.example.pethelper.db.entity.Activity

@Composable
fun GeneratedActivityPlan(
    onDismiss: () -> Unit,
    generatedActivities: List<Activity>,
    onSaveActivities: () -> Unit
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
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextMaker(
                    "Your pet's activity plan is ready!",
                    16.sp, fontWeight = FontWeight.SemiBold
                )
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(generatedActivities) { index, it ->
                        GeneratedActivity(it)
                        if (generatedActivities.lastIndex != index)
                            HorizontalDivider(
                                Modifier.fillMaxWidth(),
                                1.dp,
                                Color(0xFFF2F2F2)
                            )
                    }
                }
                ButtonMaker("Save Activity Plan", onSaveActivities)
            }
        }
    }
}

@Composable
fun GeneratedActivity(activity: Activity) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .wrapContentWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(painterResource(R.drawable.addactivityicon), null, Modifier.size(36.dp))
            Spacer(Modifier.width(10.dp))
            Column(
                Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.Start
            ) {
                TextMaker(activity.name, 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                TextMaker(activity.note, 10.sp, smallTextColor, FontWeight.SemiBold)
            }
        }
        Row(
            Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            TextMaker(activity.duration, 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}