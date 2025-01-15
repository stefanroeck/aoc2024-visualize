package day16.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import day16.SolutionStrategy

val solutionStrategyButtonOptions = SolutionStrategy.entries

@Composable
fun SolutionStrategySelectorButton(
    selectedOption: SolutionStrategy,
    selectedOptionChange: (option: SolutionStrategy) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Solution Strategy")
        SingleChoiceSegmentedButtonRow {
            solutionStrategyButtonOptions.forEachIndexed { index, option ->
                key(option.name) {
                    val isSelected = option == selectedOption
                    SegmentedButton(
                        modifier = Modifier.width(170.dp),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = solutionStrategyButtonOptions.size,
                        ),

                        onClick = { selectedOptionChange(option) },
                        selected = isSelected,
                        label = { Text(option.label) }
                    )
                }
            }
        }
    }
}