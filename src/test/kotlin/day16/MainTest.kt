package day16

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import day16.composables.AppEvent
import day16.composables.EventHandler
import day16.composables.MazeApplication
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import util.FileUtil
import util.InputUtils
import kotlin.test.assertEquals

class MainTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testApplication() {
        runTest {
            val capturedEvents = mutableListOf<AppEvent>()
            val eventHandler = object : EventHandler {
                override fun invoke(event: AppEvent) {
                    capturedEvents.add(event)
                }
            }
            val reindeerMaze = ReindeerMaze(
                InputUtils.parseLines(
                    FileUtil.readComposeResource(
                        "/day16/part1Maze.txt"
                    )
                )
            )
            composeTestRule.setContent {
                MazeApplication(reindeerMaze.maze, DpSize(10.dp, 10.dp), eventHandler)
            }

            assertEquals(0, capturedEvents.size)
        }
    }
}