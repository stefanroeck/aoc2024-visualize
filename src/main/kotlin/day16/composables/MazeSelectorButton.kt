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

data class ButtonOption(val caption: String, val resourceLocationOnClasspath: String)

val buttonOptions =
    listOf(
        ButtonOption("Small 1", "/day16/sampleMaze1.txt"),
        ButtonOption("Small 2", "/day16/sampleMaze2.txt"),
        ButtonOption("Large 1", "/day16/part1Maze.txt")
    )

@Composable
fun MazeSelectorButton(selectedOption: ButtonOption, selectedOptionChange: (ButtonOption) -> Unit) {

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select maze")
        SingleChoiceSegmentedButtonRow {
            buttonOptions.forEachIndexed { index, option ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = buttonOptions.size
                    ),

                    onClick = { selectedOptionChange(option) },
                    selected = option == selectedOption,
                    label = {
                        Text(option.caption)
                    }
                )
            }
        }
    }
}