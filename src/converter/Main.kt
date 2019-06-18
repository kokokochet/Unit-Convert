package converter

class Converter {
    /* 1 - расстояния
    *  2 - температура
    *  3 - вес
    */
    private val typesOfUnit = hashMapOf(
            "meter" to 1, "centimeter" to 1, "mile" to 1,
            "inch" to 1, "millimeter" to 1, "kilometer" to 1,
            "yard" to 1, "foot" to 1,

            "degree Fahrenheit" to 2, "degree Celsius" to 2, "Kelvin" to 2,

            "kilogram" to 3, "pound" to 3,
            "gram" to 3, "milligram" to 3,
            "pound" to 3, "ounce" to 3)
    //При добавлении дополнительных размерностей обязательно обновлять ! typesOfUnit !
    class Lengths(val inp: Double, val type: String) {
        val types = hashMapOf(
                "meter" to 1.0,
                "centimeter" to 100.0, "mile" to (1.0 / 1609.35),
                "inch" to 39.37, "millimeter" to 1000.0,
                "kilometer" to (1.0 / 1000), "yard" to (1.0 / 0.9144),
                "foot" to (1.0 / 0.3048), "inch" to (1.0 / 0.0254))

        fun convertTo(targetType: String): Double {
            if (inp < 0) {
                println("Length shouldn't be negative")
                return Double.NaN
            }
            return (inp / types[type]!!) * types[targetType]!!
        }
    }

    class Temperature(val inp: Double, val type: String) {
        val types = hashMapOf("degree Celsius" to 0.0,
                "degree Fahrenheit" to 1.0, "Kelvin" to 2.0)

        fun convertTo(targetType: String): Double {
            val celsius = when(types[type]) {
                0.0 -> inp
                1.0 -> (inp - 32.0) / 1.8
                2.0 -> inp - 273.15
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
    }

    class Weight(val inp: Double, val type: String) {
        val types = hashMapOf("gram" to 1.0,
                "kilogram" to 1000.0, "milligram" to 0.001,
                "pound" to 453.592, "ounce" to 28.3495)

        fun convertTo(targetType: String): Double {
            if (inp < 0) {
                println("Weight shouldn't be negative")
                return Double.NaN
            }
            return (inp * types[type]!!) / types[targetType]!!
        }
    }

    fun convert(inp: Double, fromTypeInput: String, toTypeInput: String): Double {
        var fromType = getNormalType(fromTypeInput)
        var toType = getNormalType(toTypeInput)
        var weCan = typesOfUnit[fromType] == typesOfUnit[toType]
        if (typesOfUnit[fromType] == null) {
            fromType = "???"
            weCan = false
        }
        if (typesOfUnit[toType] == null) {
            toType = "???"
            weCan = false
        }
        if (!weCan) {
            println("Conversion from ${getMultiForm(fromType)} to " +
                    "${getMultiForm(toType)} is impossible")
            return Double.NaN
        }
        return when(typesOfUnit[fromType]!!) {
            1 -> Lengths(inp, fromType).convertTo(toType)
            2 -> Temperature(inp, fromType).convertTo(toType)
            3 -> Weight(inp, fromType).convertTo(toType)
            else -> 0.0
        }
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

    //only after getNormalType()
    fun getMultiForm(type: String): String {
        return when(type) {
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
val conveter = Converter()

fun readUnit(): String {
    var type = sc.next().toLowerCase()
    if (type == "degrees" || type == "degree")
        type += " " + sc.next()
    return conveter.getNormalType(type)
}

fun solve(inp: Double) {
    var typeFrom = readUnit()
    sc.next()
    var typeTo = readUnit()

    val result = conveter.convert(inp, typeFrom, typeTo)

    if (result.isNaN()) return

    if (inp != 1.0) typeFrom = conveter.getMultiForm(typeFrom)
    if (result != 1.0) typeTo = conveter.getMultiForm(typeTo)
    println("$inp $typeFrom is $result $typeTo")
}

fun main(agrs: Array<String>) {
    print("Enter what you want to convert (or exit): ")
    var s = sc.next()
    while(s != "exit") {
        val x = s.toDouble()
        solve(x)
        print("Enter what you want to convert (or exit): ")
        s = sc.next()
    }
}
