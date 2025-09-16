package com.edu.core.presentation.designsystem.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.edu.core.presentation.designsystem.Poppins
import com.edu.core.presentation.designsystem.RunCounterGray


@Composable
fun ClickableAnnotatedText(
    modifier: Modifier = Modifier,
    normalText: String,
    clickableText: String,
    onClick: () -> Unit,
    normalStyle: SpanStyle = SpanStyle(
        fontFamily = Poppins,
        color = RunCounterGray
    ),
    clickableStyle: SpanStyle = SpanStyle(
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        fontFamily = Poppins
    ),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = normalStyle) {
            append("$normalText ")
            pushStringAnnotation(
                tag = "clickable_text",
                annotation = clickableText
            )
            withStyle(style = clickableStyle) {
                append(clickableText)
            }
            pop()
        }
    }

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = annotatedString,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    textLayoutResult?.let { layoutResult ->
                        val position = layoutResult.getOffsetForPosition(offset)
                        annotatedString.getStringAnnotations(
                            tag = "clickable_text",
                            start = position,
                            end = position
                        ).firstOrNull()?.let {
                            onClick()
                        }
                    }
                }
            },
        onTextLayout = { result -> textLayoutResult = result },
        style = textStyle
    )
}
