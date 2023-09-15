import java.io.*
import java.util.*


object Authors {
    fun solve(sin: Scanner, sout: PrintStream) {

        sout.println("Enter a number and a measure: ")
        val x = sin.nextInt()
        val measure = sin.next()

        if (measure.toLowerCase() == "kilometer" || measure.toLowerCase() == "kilometers" || measure.toLowerCase() == "km") {
            if (x == 1) sout.println("$x kilometer is ${x * 1000} meters")
            else sout.println("$x kilometers is ${x * 1000} meters")
        }
        else sout.println("Wrong input")
    }
}
