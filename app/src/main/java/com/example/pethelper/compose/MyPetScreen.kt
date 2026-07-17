package com.example.pethelper.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pethelper.R

@Composable
fun MyPetScreen(){
    Box(Modifier.fillMaxSize()
        .background(backgroundScreenColor)
        .padding(start = 25.dp, top = 76.dp, end = 25.dp)){
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextMaker("My Pet", 24.sp)
                Image(painter = painterResource(R.drawable.paw),
                    contentDescription = "paw icon")
            }
        }
    }
}