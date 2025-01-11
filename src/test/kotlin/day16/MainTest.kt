package day16

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import day16.composables.AppEvent
import day16.composables.EventHandler
import day16.composables.MazeApplication
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import util.FileUtil
import util.InputUtils

class MainTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testApplication() {
        val capturedEvents = mutableListOf<AppEvent>()
        val eventHandler = object : EventHandler {
            override fun invoke(event: AppEvent) {
                capturedEvents.add(event)
            }
        }
        val reindeerMaze = runBlocking {
            ReindeerMaze(
                InputUtils.parseLines(
                    FileUtil.readComposeResource(
                        "/day16/part1Maze.txt"
                    )
                )
            )
        }

        runTest {
            composeTestRule.setContent {
                MazeApplication(reindeerMaze.maze, DpSize(10.dp, 10.dp), eventHandler)
            }
            composeTestRule.onNodeWithText("Start").performClick()
            assertThat(capturedEvents).containsExactly(
                AppEvent.OnStart
            )
        }
    }
}