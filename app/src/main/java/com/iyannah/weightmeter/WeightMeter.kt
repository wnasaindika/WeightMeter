package com.iyannah.weightmeter

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.withRotation
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeightMeter(
    modifier: Modifier,
    minWeight: Int = 20,
    maxWeight: Int = 250,
    initialWeight: Int = 80,
    scaleStyle: ScaleStyle = ScaleStyle(),
    onWeightChange: (Int) -> Unit
) {
    val radius = scaleStyle.radius
    val scaleWidth = scaleStyle.scaleWidth
    var center by remember {
        mutableStateOf(Offset.Zero)
    }

    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var dragStartedAngle by remember {
        mutableFloatStateOf(0f)
    }

    var angle by remember {
        mutableFloatStateOf(0f)
    }

    var oldAngle by remember {
        mutableFloatStateOf(angle)
    }

    Canvas(modifier = modifier.pointerInput(true) {
        detectDragGestures(
            onDragStart = { offset ->
                dragStartedAngle = -atan2(
                    circleCenter.x - offset.x,
                    circleCenter.y - offset.y
                ) * (180f / PI.toFloat())

            },
            onDragEnd = {
                oldAngle = angle
            }
        ) { change, _ ->

            val touchAngle = -atan2(
                circleCenter.x - change.position.x,
                circleCenter.y - change.position.y
            ) * (180f / PI.toFloat())

            val newAngle = oldAngle + (touchAngle - dragStartedAngle)
            angle = newAngle.coerceIn(
                minimumValue = initialWeight - maxWeight.toFloat(),
                maximumValue = initialWeight - minWeight.toFloat()
            )
            onWeightChange((initialWeight - angle).toInt())
        }
    }) {
        val outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
        val innerRadius = radius.toPx() - scaleWidth.toPx() / 2f
        center = this.center
        circleCenter = Offset(
            center.x,
            scaleStyle.scaleWidth.toPx() / 2f + radius.toPx()
        )

        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                circleCenter.x,
                circleCenter.y,
                radius.toPx(),
                Paint().apply {
                    strokeWidth = scaleWidth.toPx()
                    color = Color.WHITE
                    style = Paint.Style.STROKE
                    setShadowLayer(
                        60f,
                        0f,
                        0f,
                        Color.argb(50, 0, 0, 0)
                    )
                }
            )
        }

        for (i in minWeight..maxWeight) {

            val angleInRed = (i - initialWeight + angle - 90f) * ((PI / 180f).toFloat())

            val lineType = when {
                i % 10 == 0 -> LineType.TenStepType
                i % 5 == 0 -> LineType.FiveStepType
                else -> LineType.NormalType
            }

            val color = when (lineType) {
                is LineType.NormalType -> scaleStyle.normalLineColor
                is LineType.FiveStepType -> scaleStyle.fiveStepLineColor
                is LineType.TenStepType -> scaleStyle.tenStepLineColor
            }

            val length = when (lineType) {
                is LineType.NormalType -> scaleStyle.normalLineLength
                is LineType.FiveStepType -> scaleStyle.fiveStepLineLength
                is LineType.TenStepType -> scaleStyle.tenStepLineLength
            }

            val lineStart = Offset(
                x = (outerRadius - length.toPx()) * cos(angleInRed) + circleCenter.x,
                y = (outerRadius - length.toPx()) * sin(angleInRed) + circleCenter.y
            )

            val lineEnd = Offset(
                x = outerRadius * cos(angleInRed) + circleCenter.x,
                y = outerRadius * sin(angleInRed) + circleCenter.y
            )

            drawContext.canvas.nativeCanvas.apply {
                if (lineType is LineType.TenStepType) {
                    val textRadius =
                        (outerRadius - length.toPx() - 5.dp.toPx() - scaleStyle.textSize.toPx())
                    val x = textRadius * cos(angleInRed) + circleCenter.x
                    val y = textRadius * sin(angleInRed) + circleCenter.y

                    withRotation(
                        degrees = angleInRed * (180f / PI.toFloat()) + 90f,
                        pivotX = x,
                        pivotY = y
                    ) {
                        drawText(
                            abs(i).toString(),
                            x,
                            y,
                            Paint().apply {
                                textSize = scaleStyle.textSize.toPx()
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                }

            }

            drawLine(color = color, start = lineStart, end = lineEnd, strokeWidth = 1.dp.toPx())

            val middleTop = Offset(
                x = circleCenter.x,
                y = circleCenter.y - innerRadius - scaleStyle.mainScaleLength.toPx()
            )

            val bottomLeft = Offset(
                x = circleCenter.x - 6f,
                y = circleCenter.y - innerRadius
            )

            val bottomRight = Offset(
                x = circleCenter.x + 6,
                y = circleCenter.y - innerRadius
            )

            val indicator = Path().apply {
                moveTo(middleTop.x, middleTop.y)
                lineTo(bottomLeft.x, bottomLeft.y)
                lineTo(bottomRight.x, bottomRight.y)
                lineTo(middleTop.x, middleTop.y)
            }

            drawPath(
                path = indicator,
                color = scaleStyle.mainScaleColor
            )

        }

    }

}