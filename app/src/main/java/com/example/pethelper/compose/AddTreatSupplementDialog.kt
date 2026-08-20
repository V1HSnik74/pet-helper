package com.example.pethelper.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pethelper.R

@Composable

fun AddSupplementTreatDialog(
    onDismiss: () -> Unit,
    onUpdateInfo: (name: String, petId: Int) -> Unit,
    label: String,
    icon: Int,
    placeholder: String,
    petId: Int
) {
    var value by remember { mutableStateOf("") }
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
                    Image(contentDescription = "$label icon", painter = painterResource(icon))
                    Spacer(Modifier.height(8.dp))
                    TextMaker("Add $label", 16.sp)
                    Spacer(Modifier.height(16.dp))
                    TextMaker(label, 12.sp, modifier = Modifier.align(Alignment.Start))
                    Spacer(Modifier.height(8.dp))
                    BasicTextFieldMaker(
                        value, { value = it }, placeholder, Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    ButtonMaker(
                        "Save $label",
                        { onUpdateInfo(value, petId) },
                        enabled = value.isNotEmpty()
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