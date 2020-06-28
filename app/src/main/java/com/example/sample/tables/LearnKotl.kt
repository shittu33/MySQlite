package com.example.sample.tables

import android.os.AsyncTask
import com.example.mysqlite.utils.Constants
import com.example.mysqlite.engine.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.to
import kotlin.toList

//AsyncTask
class Task : AsyncTask<String?, String?, String?>() {
    override fun doInBackground(vararg params: String?): String {
        return ""
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }
}

//lambda Expression and Anonymous Function
fun <T> lambdaExpr(): String {
//simple lamda Expression
    val do_anything = {
        //Do anything you like here and
        print("")
    }
    //still simple
    val doSome = { x: Int, y: Int -> x + y }
    //The full syntactic form of lambda expression
//    Function literals with receiver
    val fullFunction: (Int, String) -> Unit = { i, b ->
        print("")
    }
    //Anonymous function is meant for parameter
    //Anonymous functions with block body
    val minus = fun(x: Int, y: Int): Int {
        return x - y
    }
    //Anonymous functions with Expression body
    val add = fun(x: Int, y: Int): Int = x + y
    fun isOdd(x: Int) = x % 2 /*!= 0*/

    max(minus(2, 3), add(4, 5))
    max(minus(2, 3), add(4, 5))
    print(::isOdd)
    println({ a: String, b: String -> a + b })
    /**
     *  {x(Int, String) -> Unit} is a lambda expression
     * {(Int, String) -> String} is function type
     * {(Int, String)}&{(i, s)} specified the parameter
     * {-> Unit} & {-> print("just a test")}
     * specified the body of the function(Unit/return(String))**/
    val x: (Int, String) -> Unit = { i, s -> print("just a test") }
    x(2, "okay")

    val data: (T, T) -> Unit = { it: T, two: T ->
        if (it is String) {
            it.trim()
        }
    }

    val action: (Int, String) -> Unit = { value1, value2 ->
        println("$value1 and  $value2")
    }
    LamdaNoMoreListeners(action, "okay")
    /**
     *if the last parameter of a function is a function,
     *  then a lambda expression passed as the
     *  corresponding argument can be
     * placed outside the parentheses
     * */
    LamdaNoMoreListeners("okay") { value1, value2 ->
        "$value1 and $value2"
    }
    /**
     * If the lambda is the only argument to that call,
     * the parentheses can be omitted entirely
     * */
    return LamdaNoMoreListeners {
        println(it)
    }
}

fun LamdaNoMoreListeners(s: String = "", action: (String) -> Unit): String {
    for (x in 0..100) {
        action(s)
    }
    return ""
}

/**
 * {done(Int, String) -> String} is an anonymous function
 * /lamda expression
 * {(Int, String) -> String} is function type
 * {(Int, String)} specified the parameter
 * {-> String} specified the return type
 * */
fun LamdaNoMoreListeners(s: String, done: (Int, String) -> String) {
    for (x in 0..100) {
        done(2, s)
    }
//    lambdaExpr<Int>()
}

fun LamdaNoMoreListeners(done: (Int, String) -> Unit, s: String): String {
    for (x in 0..100) {
        done(2, s)
    }
    return ""
}

fun testRetified() {
    GenericSimple<Int>(3)
    val reifiedTypeForGenericReturn = reifiedTypeForGenericReturn<String>()
    val retfun = reifiedTypeForGenericReturn2<String>()
}

fun <T> GenericSimple(column: T): T {
    if (column is String) {
        column.toCharArray()
    }
    return column
}

fun genericDynamicTest() {
    GenericDynamicClass("you", 3, 4)
    genericDynamicFunction(2, 3, "me")
    "okay".by(2)
    "okay" by 4
    "okay" with 4
}

public data class GenericDynamicClass<out A, out B, out C>(
    public val first: A,
    public val second: B,
    public val third: C
)

//
fun <T, X, Y> genericDynamicFunction(me: T, you: X, we: Y) {
}
fun infixTest() {
    "SELECT" column1 {} like "this"
    val s1: String = "okay" equal  3 and "me" greatThan  8 and "you" lessThan  3
//    s1.
}
    //Mind blowing infix implementation
//public infix fun <A, B> A.row(that: B): Pair<A, B> = Pair(this, that)
infix fun <A> A.by(that: Int) = print(that)
infix fun <A> A.with(that: Int) {
}
infix fun String.column1(that: ()->Unit): String {
    return Table.getAStatementWithOperator(this, Constants.GREATER, that)
}

infix fun <B> String.column2(that: (B)->Unit): String {
    return Table.getAStatementWithOperator(this, Constants.GREATER, that)
}

//An extention function that restric the type of accepted object
//inline fun <reified T : Comparable<T>> ContentValues.getValueOfColumn(column: String): T {
//return "" as T
//}

//To avoid using inline for retified
inline fun <reified T> reifiedTypeForGenericReturn2(): ArrayList<T> =
    reifiedTypeForGenericReturn(T::class.java)

@Suppress("UNCHECKED_CAST")
fun <T> reifiedTypeForGenericReturn(type: Class<T>): ArrayList<T> {
    val arrayListOfString = arrayListOf<String>("", "")
    //Using reflection(::) to compare T
    return when (type) {
        String::class.java -> arrayListOfString as ArrayList<T>
        Int::class.java -> arrayListOf<Int>(1, 2) as ArrayList<T>
        Float::class.java -> arrayListOf<Float>(1f, 2f) as ArrayList<T>
        Double::class.java -> arrayListOf<Double>(1.4, 2.5) as ArrayList<T>
        else -> throw Exception("Unhandled return type")
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> reifiedTypeForGenericReturn(): ArrayList<T> {
    val arrayListOfString = arrayListOf<String>("", "")
    return when (T::class.java) {
        String::class.java -> arrayListOfString as ArrayList<T>
        Int::class.java -> arrayListOf<Int>(1, 2) as ArrayList<T>
        Float::class.java -> arrayListOf<Float>(1f, 2f) as ArrayList<T>
        Double::class.java -> arrayListOf<Double>(1.4, 2.5) as ArrayList<T>
        else -> throw Exception("Unhandled return type")
    }
}

/** Example of reified of specific type
 * Returns an empty array of the specified type [T].
 */
public inline fun <reified T> emptyArray(): Array<T> =
    @Suppress("UNCHECKED_CAST")
    (arrayOfNulls<T>(0) as Array<T>)

fun TestHtml() {
    html { // lambda with receiver begins here
        h1("The world is your slave")
        body("okay") // calling a method on the receiver object
        columns("ok", "now", "you", "me")
    }.apply {

    }
}

fun html(init: HTML.() -> Unit): HTML {
    HTML().apply {
        init()
        return this
    }
    //the commented code do the same as above
//    val html = HTML() // create the receiver object
//    html.init() // pass the receiver object to the lambda
//    return html
}

//fun <HTML> html2(init: HTML.() -> Unit) = initTag(HTML(), init)
//fun <T> initTag(tag: T, init: T.() -> Unit): T {
//    tag.init()
//    children.add(tag)
//    return tag
//}

class HTML {
    fun body(s: String) {}
    fun h1(s: String) {}
    fun columns(vararg s: String) {}
}

/**prevent too much overload
 * */
fun preventOverLoadTest() {
    //Whola! no parameter is passed
    var s = preventOverLoad()
    //Whola! parameter order are switched
    var c = preventOverLoad(i = 8, s = "me")
    //Whola! only call the uninitialised param
    var d = preventOverLoad(x = 8)
}

fun preventOverLoad(s: String = "something", i: Int = 0): String {
    return "$s and $i"
}

fun preventOverLoad(s: String = "something", i: Int = 0, x: Int): String {
    return "$s and $i"
}

//scope function: execute a block of code on an objects
// also,apply,let,with
fun scopeFunction() {
    val note = Note(0, "jf", "hfjd")
    note.also {
        //it is a receiver name
        it.details = "f"
        it.author = "you"
    }
    note.let {
        //it is a receiver name
        it.details = "f"
        it.author = "you"
    }
    note.apply {
        details = "naám"
        author = "kkk"
    }
    with(note) {
        this.details = "okay"
        this.author = "me"
    }
    note.details = "changed"
}

private var columns: Array<String?>? = arrayOf(null)
fun arrayCode(vararg strings: String?) {
    //copy vararg to an array using spread operator
    columns = arrayOf(*strings)
    val y = arrayOf("me", "you")
    for ((x, value) in y.withIndex()) {
        print("index is $x and value is $value")
    }
    val arrayList = ArrayList<String>(3)
    for (i in y.indices) {
        print("index is $i and value is ${y[i]}")
        for (x in arrayList) {
        }
    }
    val hashMap = HashMap<Int, String>()
    for ((x, value) in hashMap) {
        print("index is $x and value is $value")
    }
    //range as list
    val rangeList = (1..20).toList()
    //print 3 by 3 elements
    println(rangeList.windowed(3))
}

fun kotlinPairExplore(vararg m: Pair<Int, String>) {
    for ((i, pair) in m.withIndex()) {
        val firstValue = pair.toList().first()
        val secondValue = pair.toList().last()
    }
}

fun testpairCode() {
    kotlinPairExplore(2 to "4", 5 to "6", 7 to "8")
}

fun StringManipulation() {
    var sEscape = "okay \n"
    var sRaw = """okay 
        | i am doing my best sir
        | okay
        | Good of you
    """.trimMargin()
    //String template for Escape
    print("the value is ${'$'}$88 ")
    //String template for Raw
    print("""the value is ${'$'}88 """)
}

fun CollectionStudy() {
    //mutable list
    var mList = mutableListOf<Int>(1, 2, 3)
    mList.add(4);
    //imutable
    var list = listOf<String>("yes", "no")
    var set = setOf<String>("yes", "no")
    //Map
    val maps = mapOf("one" to 1, "two" to 2)
    val maps2 = mapOf<Int, String>(1 to "one", 2 to "two")
//get methods
    print(set.take(2))
    print(list.takeLast(2))
    print(set.drop(2))
    print(list.dropLast(2))
    print(list.takeUnless {
        false
    })
    print(list.dropWhile {
        true//condition
    })
    if ("one" in maps) {
        print(" One is in map with value ${maps["one"]}")
    }
    if (1 in maps.values) {
        print("1 is in map value")
    }
    val bigWords = "okay now here we are"
        .split(" ")
    val words = mutableListOf("q", "r", "r")
    bigWords.getReducedWords(words, 3)
    //minus- to remove q
    words - "q"
    //plus to add q
    words + "q"
    for (x in bigWords) {
        print("$x ")
    }
    //Grouping
    print(bigWords.groupBy {
        //get first char and toUpperCase
        it.first().toUpperCase()
        //get last index if is isLetter
        it.indexOfLast {
            it.isLetter()
        }
    })
}

//extending List method
fun List<String>.getReducedWords(
    reducedWords: MutableList<String>
    , maxLength: Int
) {
    this.filterTo(reducedWords) {
        it.length < maxLength
    }
    val phrases = setOf("a", "baba", "cat")
    reducedWords -= phrases
    //        reducedWords.minusAssign(phrases)
}

fun switchCode() {
    val x = 2;
    print(x as Int);
    when (x) {
        2 -> print("something")
        3 -> print("something")
        else -> print("someething else")
    }
}

fun rangeCode() {
    if (1 in 6 downTo 0 step 2) {
        if (1 in 1..3) {

        }
    }
}

fun extentionCall() {
    val s: String = "Hello"
    //extention call
    print(s.replace(s = "okay"))
}

fun conventions() {
    //swapping
    var a = 0
    var b = 1
    a = b.also {
        b = a
    }
    val c = TODO("Not yet specified")
    //Casting to String
    print(c as String)

}

//data class
class Result(val me: String, val you: String, val us: String)

//retturning multiple values
fun double_data(s: String = "something"): Result {
    return Result(s, "okay", "us");
}

//extention
fun String.replace(s: String = "something"): String {
    return replace(this, s)
}

//    private operator fun String.component2(): Any {
//        TODO("Not yet implemented")
//    }
//
//    private operator fun String.component1(): Any {
//        TODO("Not yet implemented")
//    }

private val stack = Stack<String>()
private fun pushToStack() {
    stack.push(Constants.WHERE)
    stack.push(Constants.ORDER_BY)
    stack.push(Constants.GROUP_BY)
    stack.push(Constants.HAVING)
}
//Uncompleted algorithm for dividing condition statement
private fun String.hasKey(keyToFind: String): Boolean {
    return contains(keyToFind, true)
}

private fun String.hasAnyKey(): Boolean {
    val key =
        findAnyOf(stack, ignoreCase = true)
    return key != null
}

private fun String.getIfContainKeyStatement(keyToFind: String/*,condition: String*/): String? {
    var rawKeyStatement: String = this
    when {
        !stack.isEmpty() && hasAnyKey() -> {
            if (hasKey(keyToFind)) {
                stack.remove(keyToFind)
                rawKeyStatement = substringAfter(keyToFind)
                if (!stack.isEmpty() && rawKeyStatement.hasAnyKey()) {
                    val keyInStack = stack.pop()
                    val substringBeforeRaw = rawKeyStatement.substringBefore(keyInStack)
                    if (!stack.isEmpty() && substringBeforeRaw.hasAnyKey()) {
                        substringBeforeRaw.getIfContainKeyStatement(stack.pop())
                    } else if (!substringBeforeRaw.hasAnyKey())
                        return substringBeforeRaw
                    else if (stack.isEmpty())
                        return substringBeforeRaw.substringBefore(keyToFind)
                } else if (!rawKeyStatement.hasAnyKey())
                    return rawKeyStatement
                else if (stack.isEmpty())
                    return rawKeyStatement.substringBefore(keyToFind)
            } else
                return when (keyToFind) {
                    Constants.WHERE -> {
                        if (this == "") null else this
                    }
                    else -> null
                }
        }
        !hasAnyKey() -> {
            return when (keyToFind) {
                Constants.WHERE -> {
                    if (this == "") null else this
                }
                else -> null
            }
        }
    }
    return null
}
