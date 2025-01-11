package util

import de.sroeck.aoc2024_visualize.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

object FileUtil {
    fun readFile(filePath: String): String {
        val input = object {}::class.java.getResource(filePath)?.readText()
        checkNotNull(input) { "cannot read input from $filePath" }
        return input
    }

    @OptIn(ExperimentalResourceApi::class)
    suspend fun readComposeResource(path: String): String {
        return Res.readBytes("files$path").decodeToString()
    }
}