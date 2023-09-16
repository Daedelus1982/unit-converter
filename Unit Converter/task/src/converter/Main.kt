package converter

fun main() {
    print("Enter a number and a measure of length: ")
    val rawLine = readln()
    val num = rawLine.substringBefore(' ').toDouble()
    val unit = rawLine.substringAfter(' ').lowercase()
    try {
        val length: Length = MyLengthFactory().createFromUnits(num, unit)
        println(MeterOutput.outputAsString(length))
    } catch (e: Exception) {
        println("Wrong input. Unknown unit $unit")
    }
}

interface Length {
    val number: Double
    val units: String
}

interface MeterConverter  {
    fun convertToMeters(): Meter
}

interface LengthFactory {
    fun createFromUnits(num: Double, unit: String): Length
}

class MeterOutput {
    companion object {
        fun outputAsString(length: Length): String {
            val meterConverter = length as MeterConverter
            val outputLength = meterConverter.convertToMeters()
            return "${length.number} ${length.units} is ${outputLength.number} ${outputLength.units}"
        }
    }
}

class MyLengthFactory : LengthFactory {
    override fun createFromUnits(num: Double, unit: String): Length {
        return when (unit) {
            in listOf("mi", "mile", "miles") -> Mile(num)
            in listOf("m", "meter", "meters") -> Meter(num)
            in listOf("cm", "centimeter",  "centimeters") -> Centimeter(num)
            in listOf("km", "kilometer", "kilometers") -> Kilometer(num)
            in listOf("mm", "millimeter", "millimeters") -> Millimeter(num)
            in listOf("yd", "yard", "yards") -> Yard(num)
            in listOf("ft", "foot", "feet") -> Foot(num)
            in listOf("in", "inch", "inches") -> Inch(num)
            else -> throw Exception("No known length type")
        }
    }
}


class Meter(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "meter" else "meters"

    override fun convertToMeters() = Meter(number)
}

class Kilometer(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "kilometer" else "kilometers"

    override fun convertToMeters() = Meter(number * 1000)
}

class Centimeter(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "centimeter" else "centimeters"

    override fun convertToMeters() = Meter(number * 0.01)
}

class Millimeter(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "millimeter" else "millimeters"

    override fun convertToMeters() = Meter(number * 0.001)
}

class Mile(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "mile" else "miles"

    override fun convertToMeters() = Meter(number * 1609.35)
}

class Yard(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "yard" else "yards"

    override fun convertToMeters() = Meter(number * 0.9144)
}

class Foot(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "foot" else "feet"

    override fun convertToMeters() = Meter(number * 0.3048)
}

class Inch(override val number: Double) : Length, MeterConverter {
    override val units = if (number == 1.0) "inch" else "inches"

    override fun convertToMeters() = Meter(number * 0.0254)
}