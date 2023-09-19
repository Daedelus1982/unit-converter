package converter

fun main() {
    while (true) {
        print("Enter what you want to convert (or exit): ")
        val words = readln().split(' ')
        if (words[0] == "exit") return

        val num = words[0].toDouble()
        val measurement: Measurement? = Measurement.createMeasurement(num, words[1].lowercase())
        val conversionUnit: Unit? = Unit.createUnit(words[3].lowercase())
        val convertedMeasure: Measurement? = measurement?.convert(conversionUnit)
        println(Output.outputAsString(measurement, conversionUnit, convertedMeasure))

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
    val equivalent: Double // for length this is equivalent of 1 meter, weight is 1 gram
    val singular: String
    val plural: String
    val alternatives: List<String>

    companion object {
        fun createUnit(unit: String): Unit? {
            return if (LengthUnit.isLengthType(unit))
                LengthUnit.getLengthFromAlternatives(unit)
            else WeightUnit.getWeightFromAlternatives(unit)
        }
    }
}
enum class LengthUnit(override val equivalent: Double, override val singular: String, override val plural: String,
                      override val alternatives: List<String>): Unit {
    METER(1.0, "meter", "meters", listOf("m", "meter", "meters")),
    MILE(1609.35, "mile", "miles", listOf("mi", "mile", "miles")),
    CENTIMETER(0.01, "centimeter", "centimeters", listOf("cm", "centimeter",  "centimeters")),
    KILOMETER(1000.0, "kilometer", "kilometers", listOf("km", "kilometer", "kilometers")),
    MILLIMETER(0.001, "millimeter", "millimeters", listOf("mm", "millimeter", "millimeters")),
    YARD(0.9144, "yard", "yards", listOf("yd", "yard", "yards")),
    FEET(0.3048, "foot", "feet", listOf("ft", "foot", "feet")),
    INCHES(0.0254, "inch", "inches", listOf("in", "inch", "inches"));

    companion object {
        fun isLengthType(unit: String): Boolean = unit in LengthUnit.values().flatMap { it.alternatives }

        fun getLengthFromAlternatives(unit: String): LengthUnit? {
            return values().firstOrNull { unit in it.alternatives }
        }
    }
}

enum class WeightUnit(override val equivalent: Double, override val singular: String, override val plural: String,
                      override val alternatives: List<String>): Unit {
    GRAM(1.0, "gram", "grams", listOf("g", "gram", "grams")),
    KILOGRAM(1000.0, "kilogram", "kilograms", listOf("kg", "kilogram", "kilograms")),
    MILLIGRAM(0.001, "milligram", "milligrams", listOf("mg", "milligram", "milligrams")),
    POUND(453.592, "pound", "pounds", listOf("lb", "pound", "pounds")),
    OUNCE(28.3495, "ounce", "ounces", listOf("oz", "ounce", "ounces"));

    companion object {
        fun isWeightType(unit: String): Boolean = unit in WeightUnit.values().flatMap { it.alternatives }

        fun getWeightFromAlternatives(unit: String): WeightUnit? {
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
            else null
        }
    }
}
class Length(override val amount: Double, override val unit: LengthUnit) : Measurement {
    override fun convert(targetUnit: Unit?): Measurement? {
        if (targetUnit !is LengthUnit) return null
        val meters = amount * unit.equivalent
        return Length(meters / targetUnit.equivalent, targetUnit)
    }
}

class Weight(override val amount: Double, override val unit: WeightUnit): Measurement {
    override fun convert(targetUnit: Unit?): Measurement? {
        if (targetUnit !is WeightUnit) return null
        val grams = amount * unit.equivalent
        return Weight(grams / targetUnit.equivalent, targetUnit)
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
