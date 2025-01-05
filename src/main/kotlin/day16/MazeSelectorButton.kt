package day16

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

data class ButtonOption(val caption: String, val resourceLocationOnClasspath: String)

val buttonOptions =
    listOf(
        ButtonOption("Sample 2", "/day16/sampleMaze.txt"),
        ButtonOption("Part 1", "/day16/part1Maze.txt")
    )

@Composable
fun MazeSelectorButton(selectedOption: ButtonOption, selectedOptionChange: (ButtonOption) -> Unit) {

    SingleChoiceSegmentedButtonRow {
        buttonOptions.forEachIndexed { index, option ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = buttonOptions.size
                ),

                onClick = { selectedOptionChange(option) },
                selected = option == selectedOption,
                label = { Text(option.caption) }
            )
        }
    }
}