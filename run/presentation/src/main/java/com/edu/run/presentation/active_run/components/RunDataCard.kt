package com.edu.run.presentation.active_run.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.core.presentation.designsystem.BorderSubtle
import com.edu.core.presentation.designsystem.LocationIcon
import com.edu.core.presentation.designsystem.RunCounterBlack
import com.edu.core.presentation.designsystem.RunCounterCyan
import com.edu.core.presentation.designsystem.RunCounterDarkGray
import com.edu.core.presentation.designsystem.RunCounterGray
import com.edu.core.presentation.designsystem.RunCounterGray40
import com.edu.core.presentation.designsystem.RunCounterTheme
import com.edu.core.presentation.designsystem.RunCounterWhite
import com.edu.core.presentation.designsystem.RunIcon
import com.edu.core.presentation.ui.formatted
import com.edu.core.presentation.ui.toFormattedKm
import com.edu.core.presentation.ui.toFormattedPace
import com.edu.run.domain.RunData
import com.edu.run.presentation.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun RunDataCard(
    modifier: Modifier = Modifier,
    elapsedTime: Duration,
    runData: RunData
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        RunCounterDarkGray.copy(alpha = 0.97f),
                        RunCounterBlack
                    )
                )
            )
            .padding(horizontal = 28.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(RunCounterGray40)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.duration).uppercase(),
            color = RunCounterGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = elapsedTime.formatted(),
            color = RunCounterCyan,
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(
            color = BorderSubtle,
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                icon = LocationIcon,
                label = stringResource(id = R.string.distance).uppercase(),
                value = (runData.distanceMeters / 1000.0).toFormattedKm(),
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(BorderSubtle)
            )

            StatItem(
                icon = RunIcon,
                label = stringResource(id = R.string.pace).uppercase(),
                value = elapsedTime.toFormattedPace(distanceKm = runData.distanceMeters / 1000.0),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = RunCounterCyan,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            color = RunCounterGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
        )
        Text(
            text = value,
            color = RunCounterWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun RunDataCardPreview() {
    RunCounterTheme {
        RunDataCard(
            elapsedTime = 10.minutes,
            runData = RunData(
                distanceMeters = 34256,
                pace = 3.minutes,
            )
        )
    }
}
