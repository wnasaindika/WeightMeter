package com.iyannah.weightmeter

sealed interface LineType {
    data object NormalType : LineType
    data object FiveStepType : LineType
    data object TenStepType : LineType
}