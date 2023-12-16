package com.iyannah.weightmeter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var weight by remember {
                mutableIntStateOf(80)
            }

            Box(modifier = Modifier.fillMaxSize()) {

                Text(
                    text = "Current Weight \n $weight",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp)
                        .align(Alignment.TopCenter),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                WeightMeter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 200.dp)
                        .height(300.dp)
                        .align(Alignment.TopCenter)
                ) {
                    weight = it
                }
            }
        }
    }
}