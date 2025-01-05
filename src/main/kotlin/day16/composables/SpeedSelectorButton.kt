package day16.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

data class SpeedButtonOption(val caption: String, val delay: Long)

val speedButtonOptions =
    listOf(
        SpeedButtonOption("Fast", 50),
        SpeedButtonOption("Faster", 20),
        SpeedButtonOption("Fastest", 10)
    )

@Composable
fun SpeedSelectorButton(selectedOption: SpeedButtonOption, selectedOptionChange: (SpeedButtonOption) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Speed")
        SingleChoiceSegmentedButtonRow {
            speedButtonOptions.forEachIndexed { index, option ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = speedButtonOptions.size
                    ),

                    onClick = { selectedOptionChange(option) },
                    selected = option == selectedOption,
                    label = { Text(option.caption) }
                )
            }
        }
    }
}