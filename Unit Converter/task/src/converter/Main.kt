package converter

fun main() {
    print("Enter a number and a measure: ")
    val rawLine = readln()
    val num = rawLine.substringBefore(' ').toInt()
    val unit = rawLine.substringAfter(' ').lowercase()
    println(
        if (!(unit == "km" || unit == "kilometer" || unit == "kilometers"))
            "Wrong input"
        else {
            val singularUnit = if (num == 1) "kilometer" else "kilometers"
            "$num $singularUnit is ${num * 1000} meters"
        }
    )
}
