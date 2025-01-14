package day16

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import day16.composables.MazeApplication
import day16.composables.SemanticPropertyKeys
import day16.composables.SemanticPropertyKeys.MazeTile.MazeTileState
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MazeApplicationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `hit start on select screen`() {
        runTest {
            composeTestRule.setContent {
                MazeApplication(DpSize(10.dp, 10.dp))
            }
            composeTestRule.onNodeWithText("Small 1").performClick()
            composeTestRule.onNodeWithText("Start").performClick()
            composeTestRule.onAllNodesWithTag("mazeTile").assertCountEquals(225)
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `solve smallest maze`() {
        runTest {
            composeTestRule.setContent {
                MazeApplication(DpSize(10.dp, 10.dp))
            }
            composeTestRule.onNodeWithText("Small 1").performClick()
            composeTestRule.onNodeWithText("Show Movement").performClick()
            composeTestRule.onNodeWithText("Start").performClick()
            composeTestRule.onNodeWithText("Start").performClick()
            composeTestRule.waitUntilExactlyOneExists(hasText("Finished", substring = true))
            composeTestRule.onNodeWithText("Found solutions: 4", substring = true).assertExists()

            composeTestRule.onAllNodesWithTag("mazeTile")
                .filter(hasMazeTileState(MazeTileState.ShortestSolution))
                .assertCountEquals(42)
            composeTestRule.onAllNodesWithTag("mazeTile")
                .filter(hasMazeTileState(MazeTileState.OtherSolutions))
                .assertCountEquals(40)
        }
    }

    private fun hasMazeTileState(state: MazeTileState) =
        SemanticsMatcher("has state $state") {
            it.config[SemanticPropertyKeys.MazeTile.State].contains(state)
        }
}