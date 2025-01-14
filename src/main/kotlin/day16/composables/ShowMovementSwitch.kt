package day16.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun ShowMovementSwitch(
    selectedOption: Boolean,
    selectedOptionChange: (option: Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = selectedOption,
                onCheckedChange = {
                    selectedOptionChange(it)
                },
                modifier = Modifier.scale(0.67f)
            )
            Text(
                "Show Movement",
                modifier = Modifier.clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = true,
                    onClick = { selectedOptionChange(!selectedOption) })
            )
        }
    }

}