package util

object InputUtils {

    fun parseLines(input: String): List<String> = input
        .trimIndent()
        .split("\n")
        .map { it.trim() }
        .filterNot { it.isEmpty() }
}