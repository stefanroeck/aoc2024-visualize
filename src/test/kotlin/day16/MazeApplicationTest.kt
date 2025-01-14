package day16

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import day16.composables.MazeApplication
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
}