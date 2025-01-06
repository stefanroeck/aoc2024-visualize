package day16.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

data class SpeedButtonOption(val caption: String, val delay: Long)

data class SpeedSelectorOption(val buttonOption: SpeedButtonOption, val showMovement: Boolean)

val speedButtonOptions =
    listOf(
        SpeedButtonOption("Fast", 50),
        SpeedButtonOption("Faster", 20),
        SpeedButtonOption("Fastest", 10)
    )

@Composable
fun SpeedSelectorButton(
    selectedOption: SpeedSelectorOption,
    selectedOptionChange: (option: SpeedSelectorOption) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("UI update speed")
        SingleChoiceSegmentedButtonRow {
            speedButtonOptions.forEachIndexed { index, option ->
                key(option.delay) {
                    val isSelected = option == selectedOption.buttonOption
                    val showMovement = selectedOption.showMovement
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = speedButtonOptions.size,
                        ),

                        onClick = {
                            selectedOptionChange(SpeedSelectorOption(option, showMovement))
                        },
                        selected = isSelected,
                        label = { Text(option.caption) }
                    )
                }
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = selectedOption.showMovement,
                onCheckedChange = {
                    selectedOptionChange(SpeedSelectorOption(selectedOption.buttonOption, it))
                },
                modifier = Modifier.scale(0.67f)
            )
            Text("Show Movement")
        }
    }

}