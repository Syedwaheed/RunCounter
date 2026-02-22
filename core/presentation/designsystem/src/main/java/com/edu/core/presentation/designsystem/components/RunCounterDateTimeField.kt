package com.edu.core.presentation.designsystem.components

import android.R
import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edu.core.presentation.designsystem.CalendarIcon
import com.edu.core.presentation.designsystem.RunIcon
import java.time.LocalDate

@Composable
fun RunCounterDateTimeField(
    modifier: Modifier = Modifier,
    value:String,
    onClick:() -> Unit,
    startIcon: ImageVector?,
    hint: String,
    title: String?,
    error: String? = null
) {
    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            if(title !=null){
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (error != null){
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
           modifier = Modifier
               .fillMaxWidth()
               .clip(RoundedCornerShape(16.dp))
               .background(
                   color = MaterialTheme.colorScheme.surface
               )
               .border(
                   width = 1.dp,
                   color = if(error != null){
                       MaterialTheme.colorScheme.error
                   }else{
                       MaterialTheme.colorScheme.surface
                   },
                   shape = RoundedCornerShape(16.dp)
               )
               .clickable{onClick()}
               .padding(12.dp)

        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if(startIcon != null){
                    Icon(
                        imageVector = startIcon,
                        modifier = Modifier.wrapContentSize().size(20.dp),
                        contentDescription = null
                    )
                }

                Text(
                    modifier = Modifier.weight(1f),
                    text = value.ifEmpty { hint },
                    color = if(value.isNotEmpty()){
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }else{
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f)
                    }

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RunCounterDateTimeFieldPreview() {
    RunCounterDateTimeField(
        value = "12.12",
        onClick = {},
        startIcon = CalendarIcon,
        hint = "example@test.com",
        title = "Date",
        error = null
    )
}