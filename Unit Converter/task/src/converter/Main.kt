package converter

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val words = readln().split(' ')
        if (words[0] == "exit") return

        try {
            val num = words[0].toDouble()
            val fromUnits = if (words[1].lowercase().startsWith("degree")) "${words[1]} ${words[2]}".lowercase() else words[1].lowercase()
            if (LengthUnit.isLengthType(fromUnits) && num < 0.0) throw IllegalStateException("Length shouldn't be negative.")
            if (WeightUnit.isWeightType(fromUnits) && num < 0.0) throw IllegalStateException("Weight shouldn't be negative.")
            val toUnits = if (words[words.lastIndex - 1].lowercase().startsWith("degree"))
                "${words[words.lastIndex - 1]} ${words[words.lastIndex]}".lowercase() else words[words.lastIndex].lowercase()
            val measurement: Measurement? = Measurement.createMeasurement(num, fromUnits)
            val conversionUnit: Unit? = Unit.createUnit(toUnits)
            val convertedMeasure: Measurement? = measurement?.convert(conversionUnit)
            println(Output.outputAsString(measurement, conversionUnit, convertedMeasure))
        } catch (nfe: NumberFormatException) {
            println("Parse error")
        } catch (ise: IllegalStateException) {
            println(ise.message)
        }

        println()
    }
}

class Output {
    companion object {
        fun outputAsString(measurement: Measurement?, conversionUnit: Unit?, convertedMeasure: Measurement?): String {
            return try {
                if (convertedMeasure == null) throw Exception("if conversion failed then normal execution must fail")
                val fromUnit = if (measurement?.amount == 1.0) measurement.unit.singular else measurement?.unit?.plural
                val toUnit = if (convertedMeasure.amount == 1.0) convertedMeasure.unit.singular else convertedMeasure.unit.plural
                "${measurement?.amount} $fromUnit is ${convertedMeasure.amount} $toUnit"
            } catch (ex: Exception) {
                val fromUnit = measurement?.unit?.plural ?: "???"
                val toUnit = conversionUnit?.plural ?: "???"
                "Conversion from $fromUnit to $toUnit is impossible"
            }
        }
    }
}

interface Unit {
    val toBase: (Double) -> Double // for length, base is meters, weight is grams, temperature is Celsius
    val singular: String
    val plural: String
    val alternatives: List<String>

    companion object {
        fun createUnit(unit: String): Unit? {
            return if (LengthUnit.isLengthType(unit))
                LengthUnit.getLengthFromAlternatives(unit)
            else if (WeightUnit.isWeightType(unit))
                WeightUnit.getWeightFromAlternatives(unit)
            else TemperatureUnit.getTemperatureFromAlternatives(unit)
        }
    }
}
enum class LengthUnit(override val toBase: (Double) -> Double, override val singular: String,
                      override val plural: String, override val alternatives: List<String>): Unit {
    METER({it * 1.0}, "meter", "meters", listOf("m", "meter", "meters")),
    MILE({it * 1609.35}, "mile", "miles", listOf("mi", "mile", "miles")),
    CENTIMETER({it * 0.01}, "centimeter", "centimeters", listOf("cm", "centimeter",  "centimeters")),
    KILOMETER({it * 1000.0}, "kilometer", "kilometers", listOf("km", "kilometer", "kilometers")),
    MILLIMETER({it * 0.001}, "millimeter", "millimeters", listOf("mm", "millimeter", "millimeters")),
    YARD({it * 0.9144}, "yard", "yards", listOf("yd", "yard", "yards")),
    FEET({it * 0.3048}, "foot", "feet", listOf("ft", "foot", "feet")),
    INCHES({it * 0.0254}, "inch", "inches", listOf("in", "inch", "inches"));

    companion object {
        fun isLengthType(unit: String): Boolean = unit in LengthUnit.values().flatMap { it.alternatives }

        fun getLengthFromAlternatives(unit: String): LengthUnit? {
            return values().firstOrNull { unit in it.alternatives }
        }
    }
}

enum class WeightUnit(override val toBase: (Double) -> Double, override val singular: String,
    override val plural: String, override val alternatives: List<String>): Unit {
    GRAM({it * 1.0}, "gram", "grams", listOf("g", "gram", "grams")),
    KILOGRAM({it * 1000.0}, "kilogram", "kilograms", listOf("kg", "kilogram", "kilograms")),
    MILLIGRAM({it * 0.001}, "milligram", "milligrams", listOf("mg", "milligram", "milligrams")),
    POUND({it * 453.592}, "pound", "pounds", listOf("lb", "pound", "pounds")),
    OUNCE({it * 28.3495}, "ounce", "ounces", listOf("oz", "ounce", "ounces"));

    companion object {
        fun isWeightType(unit: String): Boolean = unit in WeightUnit.values().flatMap { it.alternatives }

        fun getWeightFromAlternatives(unit: String): WeightUnit? {
            return values().firstOrNull { unit in it.alternatives }
        }
    }
}

enum class TemperatureUnit(
    override val toBase: (Double) -> Double, override val singular: String,
    override val plural: String, override val alternatives: List<String>) : Unit {
        CELSIUS({it * 1.0}, "degree Celsius", "degrees Celsius", listOf("degree celsius", "degrees celsius", "celsius", "dc", "c")),
    FAHRENHEIT({(it - 32.0) * 5 / 9}, "degree Fahrenheit", "degrees Fahrenheit", listOf("degree fahrenheit", "degrees fahrenheit",
        "fahrenheit", "df", "f")),
    KELVIN({it - 273.15}, "kelvin", "kelvins", listOf("k", "kelvin", "kelvins"));

    companion object {
        fun isTemperatureType(unit: String): Boolean = unit in TemperatureUnit.values().flatMap { it.alternatives }

        fun getTemperatureFromAlternatives(unit: String): TemperatureUnit? {
            return values().firstOrNull { unit in it.alternatives }
        }
    }
}

interface Measurement {
    val amount: Double
    val unit: Unit

    fun convert(targetUnit: Unit?): Measurement?

    companion object {
        fun createMeasurement(amount: Double, unit: String): Measurement? {
            return if (LengthUnit.isLengthType(unit))
                MyLengthFactory().createFromUnits(amount, unit)
            else if (WeightUnit.isWeightType(unit))
                MyWeightFactory().createFromUnits(amount, unit)
            else if (TemperatureUnit.isTemperatureType(unit))
                MyTemperatureFactory().createFromUnits(amount, unit)
            else null

        }
    }
}
class Length(override val amount: Double, override val unit: LengthUnit) : Measurement {
    override fun convert(targetUnit: Unit?): Measurement? {
        if (targetUnit !is LengthUnit) return null

        return Length(unit.toBase(amount) / targetUnit.toBase(1.0), targetUnit)
    }
}

class Weight(override val amount: Double, override val unit: WeightUnit): Measurement {
    override fun convert(targetUnit: Unit?): Measurement? {
        if (targetUnit !is WeightUnit) return null

        return Weight(unit.toBase(amount) / targetUnit.toBase(1.0), targetUnit)
    }
}

class Temperature(override val amount: Double, override val unit: TemperatureUnit) : Measurement {
    private enum class CONVERTER(val f: (Double) -> Double) {
        CTF({it * 9 / 5 + 32}),
        CTK({it + 273.15}),
        FTK({(it + 459.67) * 5 / 9}),
        KTF({it * 9 / 5 - 459.67})
    }
    override fun convert(targetUnit: Unit?): Measurement? {
        if (targetUnit !is TemperatureUnit) return null

        return when  {
            targetUnit == TemperatureUnit.CELSIUS -> Temperature(unit.toBase(amount), targetUnit)
            unit == TemperatureUnit.CELSIUS && targetUnit == TemperatureUnit.FAHRENHEIT ->
                Temperature(CONVERTER.CTF.f(amount), targetUnit)
            unit == TemperatureUnit.CELSIUS && targetUnit == TemperatureUnit.KELVIN ->
                Temperature(CONVERTER.CTK.f(amount), targetUnit)
            unit == TemperatureUnit.FAHRENHEIT && targetUnit == TemperatureUnit.FAHRENHEIT ->
                Temperature(amount, targetUnit)
            unit == TemperatureUnit.FAHRENHEIT && targetUnit == TemperatureUnit.KELVIN ->
                Temperature(CONVERTER.FTK.f(amount), targetUnit)
            unit == TemperatureUnit.KELVIN && targetUnit == TemperatureUnit.KELVIN ->
                Temperature(amount, targetUnit)
            unit == TemperatureUnit.KELVIN && targetUnit == TemperatureUnit.FAHRENHEIT ->
                Temperature(CONVERTER.KTF.f(amount), targetUnit)
            else -> null
        }
    }
}

interface LengthFactory {
    fun createFromUnits(num: Double, unit: String): Length
}

class MyLengthFactory : LengthFactory {
    override fun createFromUnits(num: Double, unit: String): Length {
        for (lengthUnit in LengthUnit.values()) {
            if (unit in lengthUnit.alternatives) return Length(num, lengthUnit)
        }

        throw InstantiationException("unit passed is not a valid type")
    }
}

interface WeightFactory {
    fun createFromUnits(num: Double, unit: String): Weight
}

class MyWeightFactory : WeightFactory {
    override fun createFromUnits(num: Double, unit: String): Weight {
        for (weightUnit in WeightUnit.values()) {
            if (unit in weightUnit.alternatives) return Weight(num, weightUnit)
        }

        throw InstantiationException("unit passed is not a valid type")
    }
}

interface TemperatureFactory {
    fun createFromUnits(num: Double, unit: String): Temperature
}

class MyTemperatureFactory : TemperatureFactory {
    override fun createFromUnits(num: Double, unit: String): Temperature {
        for (temperatureUnit in TemperatureUnit.values()) {
            if (unit in temperatureUnit.alternatives) return Temperature(num, temperatureUnit)
        }

        throw InstantiationException("unit passed is not a valid type")
    }
}
