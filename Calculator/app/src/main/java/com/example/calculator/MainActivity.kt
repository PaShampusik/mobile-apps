package com.example.calculator

import net.objecthunter.exp4j.ExpressionBuilder
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val result: TextView = findViewById<TextView>(R.id.result)
        val operation: TextView = findViewById<TextView>(R.id.operation)

        val sqrt: TextView = findViewById<TextView>(R.id.b_sqrt)
        val log2: TextView = findViewById<TextView>(R.id.b_log2)
        val ln: TextView = findViewById<TextView>(R.id.b_ln)
        val leftb: TextView = findViewById<TextView>(R.id.b_leftb)
        val rightb: TextView = findViewById<TextView>(R.id.b_rightb)

        val degree: TextView = findViewById<TextView>(R.id.b_degree)
        val ac: TextView = findViewById<TextView>(R.id.b_AC)
        val back: TextView = findViewById<TextView>(R.id.b_back)
        val percent: TextView = findViewById<TextView>(R.id.b_percent)
        val divide: TextView = findViewById<TextView>(R.id.b_divide)

        val sin: TextView = findViewById<TextView>(R.id.b_sin)
        val seven: TextView = findViewById<TextView>(R.id.b_seven)
        val eight: TextView = findViewById<TextView>(R.id.b_eight)
        val nine: TextView = findViewById<TextView>(R.id.b_nine)
        val multy: TextView = findViewById<TextView>(R.id.b_multy)

        val cos: TextView = findViewById<TextView>(R.id.b_cos)
        val four: TextView = findViewById<TextView>(R.id.b_four)
        val fife: TextView = findViewById<TextView>(R.id.b_fife)
        val six: TextView = findViewById<TextView>(R.id.b_six)
        val minus: TextView = findViewById<TextView>(R.id.b_minus)

        val pi: TextView = findViewById<TextView>(R.id.b_pi)
        val one: TextView = findViewById<TextView>(R.id.b_one)
        val two: TextView = findViewById<TextView>(R.id.b_two)
        val three: TextView = findViewById<TextView>(R.id.b_three)
        val plus: TextView = findViewById<TextView>(R.id.b_plus)

        val epsilon: TextView = findViewById<TextView>(R.id.b_epsilon)
        val zeros: TextView = findViewById<TextView>(R.id.b_zeros)
        val point: TextView = findViewById<TextView>(R.id.b_point)
        val zero: TextView = findViewById<TextView>(R.id.b_zero)
        val equal: TextView = findViewById<TextView>(R.id.b_equal)

        zero.setOnClickListener { operation.append("0") }
        point.setOnClickListener { operation.append(".") }
        zeros.setOnClickListener { operation.append("000") }
        epsilon.setOnClickListener { operation.append("e") }
        equal.setOnClickListener {
            val optext = operation.text.toString()
            if (optext != "") {
                try {
                    val expr = ExpressionBuilder(operation.text.toString()).build()
                    val res = expr.evaluate()
                    val longres = res.toLong()
                    if (longres.toDouble() == res) {
                        result.text = longres.toString()
                    } else {
                        result.text = res.toString()
                    }
                } catch (e: Exception) {
                    result.text = "Error"
                }
            }
        }

        plus.setOnClickListener { operation.append("+") }
        three.setOnClickListener { operation.append("3") }
        two.setOnClickListener { operation.append("2") }
        one.setOnClickListener { operation.append("1") }
        pi.setOnClickListener { operation.append("3.1415") }

        minus.setOnClickListener { operation.append("-") }
        six.setOnClickListener { operation.append("6") }
        fife.setOnClickListener { operation.append("5") }
        four.setOnClickListener { operation.append("4") }
        cos.setOnClickListener { operation.append("cos(") }

        multy.setOnClickListener { operation.append("*") }
        nine.setOnClickListener { operation.append("9") }
        eight.setOnClickListener { operation.append("8") }
        seven.setOnClickListener { operation.append("7") }
        sin.setOnClickListener { operation.append("sin(") }

        sqrt.setOnClickListener { operation.append("sqrt(") }
        log2.setOnClickListener { operation.append("log2(") }
        ln.setOnClickListener { operation.append("ln(") }
        leftb.setOnClickListener { operation.append("(") }
        rightb.setOnClickListener { operation.append(")") }

        divide.setOnClickListener { operation.append("/") }
        percent.setOnClickListener { operation.append("%") }
        back.setOnClickListener {
            val s = operation.text.toString()
            if (s != "") {
                operation.text = s.substring(0, s.length - 1)
            }
            ac.setOnClickListener {
                operation.text = ""
                result.text = ""
            }
            degree.setOnClickListener { operation.append("^") }

            result.setOnClickListener {
                val restext = result.text.toString()
                if (restext != "Error" && restext != "") {
                    operation.text = restext
                    result.text = ""
                }
            }
        }
    }
}
