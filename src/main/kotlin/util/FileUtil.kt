package util

object FileUtil {
    fun readFile(filePath: String): String {
        val input = object {}::class.java.getResource(filePath)?.readText()
        checkNotNull(input) { "cannot read input from $filePath" }
        return input
    }
}