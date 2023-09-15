import org.hyperskill.hstest.stage.StageTest
import org.hyperskill.hstest.testcase.CheckResult


/** TestCase, based on authors solution output. */
fun authorsCase(input: String, isPrivate: Boolean = false) =
        authorsCaseFromFun(Authors::solve, input, isPrivate)

class Task2Test : StageTest<OutputClue>() {

    override fun generate() = listOf(
            authorsCase("25 kilometers"),
            authorsCase("26 KM", isPrivate = true),
            authorsCase("13 km", isPrivate = true),
            authorsCase("1 kilometer"),
            authorsCase("1 kIlOmeter", isPrivate = true),
            authorsCase("17 KILOMETERS"),
            authorsCase("13 kn", isPrivate = true)
    )

    override fun check(reply: String, clue: OutputClue): CheckResult {
        // compare output the clue output and reply with our custom comparer.
        val checkResult = WordComparer(clue.output, reply).compare()

        if (clue.isPrivate)
            return checkResult.ciphered()
        return checkResult
    }
}
