package com.edu.core.presentation.designsystem.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowDpSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.edu.core.presentation.designsystem.RunCounterTheme

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    hasToolbar: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {

    val windowSize = currentWindowDpSize()
    val density = LocalDensity.current
    val screenHeightDp = windowSize.height
    val screenWidthDp = windowSize.width

    val screenWidthPx = with(density){
        screenWidthDp.toPx()
    }
    val screenHeightPx = with(density){
        screenHeightDp.toPx()
    }

    val smallDimensionDp = minOf(
        screenWidthDp,
        screenHeightDp
    )
    val smallDimensionPx = with(density){
        smallDimensionDp.toPx()
    }
    val primaryColor = MaterialTheme.colorScheme.primary
    val isAtLeastAndroid12 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isAtLeastAndroid12) {
                        Modifier.blur(
                            smallDimensionDp / 3f
                        )
                    } else Modifier
                )
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (isAtLeastAndroid12) primaryColor else primaryColor.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.background
                        ),
                        center = Offset(
                            x = screenWidthPx / 2f,
                            y = -100f
                        ),
                        radius = smallDimensionPx / 2f
                    )

                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if(hasToolbar){
                        Modifier
                    }else{
                        Modifier.systemBarsPadding()
                    }
                )
        ){
            content()
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun GradientBackgroundPreview() {
    RunCounterTheme {
        GradientBackground(
            modifier = Modifier.fillMaxSize()
        ) {

        }
    }
}