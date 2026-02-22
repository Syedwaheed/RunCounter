package com.edu.run.presentation.charts.mappers

import com.edu.core.domain.run.Run
import com.edu.run.presentation.charts.ChartsScreenState
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toChartLabel() : String{
    return dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("dd MMM"))
}

fun ChartsScreenState.toCartesianValues(): CartesianValueFormatter{
    return CartesianValueFormatter{ context, value, vertical ->
        val index = value.toInt()
        this.runLabels.getOrElse(index) { "-" }
    }
}