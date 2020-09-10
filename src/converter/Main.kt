package converter

import kotlin.collections.HashMap

class Converter {

    enum class UnitType {

        LENGTH {
            private val types = hashMapOf(
                "meter" to 1.0,
                "centimeter" to 100.0, "mile" to (1.0 / 1609.35),
                "inch" to 39.37, "millimeter" to 1000.0,
                "kilometer" to (1.0 / 1000), "yard" to (1.0 / 0.9144),
                "foot" to (1.0 / 0.3048), "inch" to (1.0 / 0.0254))

            override fun convertTo(fromType: String, targetType: String, value: Double): Double {
                if (value < 0) {
                    println("Length shouldn't be negative")
                    return Double.NaN
                }
                return (value / types[fromType]!!) * types[targetType]!!
            }

            override fun isThis(type: String) = check(type, types)
        },

        TEMPERATURE {
            private val types = hashMapOf("degree Celsius" to 0.0,
                "degree Fahrenheit" to 1.0, "Kelvin" to 2.0)

            override fun convertTo(fromType: String, targetType: String, value: Double): Double {
                val celsius = when(types[fromType]) {
                    0.0 -> value
                    1.0 -> (value - 32.0) / 1.8
                    2.0 -> value - 273.15
                    else -> 0.0
                }
                if (celsius + 273.15 < 0) {
                    println("Temperature can't be < 0K")
                    return Double.NaN
                }
                return when(types[targetType]) {
                    0.0 -> celsius
                    1.0 -> celsius * 1.8 + 32
                    2.0 -> celsius + 273.15
                    else -> Double.NaN
                }
            }

            override fun isThis(type: String) = check(type, types)
        },

        WEIGH {
            private val types = hashMapOf("gram" to 1.0,
                "kilogram" to 1000.0, "milligram" to 0.001,
                "pound" to 453.592, "ounce" to 28.3495)

            override fun convertTo(fromType: String, targetType: String, value: Double): Double {
                if (value < 0) {
                    println("Weight shouldn't be negative")
                    return Double.NaN
                }
                return (value * types[fromType]!!) / types[targetType]!!
            }

            override fun isThis(type: String) = check(type, types)
        };

        abstract fun isThis(type: String): Boolean
        abstract fun convertTo(fromType: String, targetType: String, value: Double): Double

        fun check(type: String, types: HashMap<String, out Any>): Boolean = types.containsKey(type)
    }

    private fun getType(type: String): UnitType? = when {
        UnitType.WEIGH.isThis(type) -> UnitType.WEIGH
        UnitType.LENGTH.isThis(type) -> UnitType.LENGTH
        UnitType.TEMPERATURE.isThis(type) -> UnitType.TEMPERATURE
        else -> null
    }

    fun convert(inp: Double, fromTypeInput: String, toTypeInput: String): Double {
        var fromType = getNormalType(fromTypeInput)
        var toType = getNormalType(toTypeInput)
        var weCan = getType(fromType) == getType(toType)
        if (getType(fromType) == null) {
            fromType = "???"
            weCan = false
        }
        if (getType(toType) == null) {
            toType = "???"
            weCan = false
        }
        if (!weCan) {
            println("Conversion from ${getMultiForm(fromType)} to " +
                    "${getMultiForm(toType)} is impossible")
            return Double.NaN
        }
        return getType(fromType)!!.convertTo(fromType, toType, inp)
    }

    fun getNormalType(type: String): String {
        return when(type.toLowerCase()) {
            "m", "meters" -> "meter"
            "km", "kilometers" -> "kilometer"
            "cm", "centimeters" -> "centimeter"
            "mm", "millimeters" -> "millimeter"
            "mi", "miles" -> "mile"
            "yd", "yards" -> "yard"
            "ft", "feet" -> "foot"
            "in", "inches" -> "inch"
            "g", "grams" -> "gram"
            "kg", "kilograms" -> "kilogram"
            "mg", "milligrams" -> "milligram"
            "lb", "pounds" -> "pound"
            "oz", "ounces" -> "ounce"
            "degree celsius", "degrees celsius", "celsius", "dc", "c" ->
                "degree Celsius"
            "degree fahrenheit", "degrees fahrenheit", "fahrenheit", "df","f" ->
                "degree Fahrenheit"
            "kelvin", "kelvins", "k" -> "Kelvin"
            else -> type.toLowerCase()
        }
    }

    fun getMultiForm(type: String): String {
        return when(getNormalType(type)) {
            "inch" -> "inches"
            "foot" -> "feet"
            "degree Fahrenheit" -> "degrees Fahrenheit"
            "degree Celsius" -> "degrees Celsius"
            "???" -> "???"
            else -> type + "s"
        }
    }
}

val sc = java.util.Scanner(System.`in`)
val converter = Converter()

fun readUnit(): String {
    var type = sc.next().toLowerCase()
    if (type == "degrees" || type == "degree")
        type += " " + sc.next()
    return converter.getNormalType(type)
}

fun solve(inp: Double) {
    var typeFrom = readUnit()
    sc.next()
    var typeTo = readUnit()

    val result = converter.convert(inp, typeFrom, typeTo)

    if (result.isNaN()) return

    if (inp != 1.0) typeFrom = converter.getMultiForm(typeFrom)
    if (result != 1.0) typeTo = converter.getMultiForm(typeTo)
    println("$inp $typeFrom is $result $typeTo")
}

fun main() {
    print("Enter what you want to convert (or exit): ")
    var s = sc.next()
    while(s != "exit") {
        val x = s.toDouble()
        solve(x)
        print("Enter what you want to convert (or exit): ")
        s = sc.next()
    }
}
