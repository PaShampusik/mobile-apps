package com.example.calculator

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private var drawerLayout: DrawerLayout? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    private lateinit var themeSwitch: Switch
    private lateinit var button: Button
    private lateinit var result: TextView
    private lateinit var operation: TextView

    private var isDarkTheme: Boolean = false
    var db = FirebaseFirestore.getInstance()
    var themeRef = db.collection("themes").document("userTheme")
    var historyRef = db.collection("history").document("operations")

    var history_counter = 0
    private fun setThemeColors(isDarkTheme: Boolean) {
        val primaryColor: Int
        val secondaryColor: Int
        val textColor: Int
        if (isDarkTheme) {
            primaryColor = resources.getColor(R.color.black)
            secondaryColor = resources.getColor(R.color.theme_dark_secondary)
            textColor = resources.getColor(R.color.white)
        } else {
            primaryColor = resources.getColor(R.color.white)
            secondaryColor = resources.getColor(R.color.white)
            textColor = resources.getColor(R.color.black)
        }

        // Установка цвета статус-бара
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = primaryColor


        // Применение цветов к элементам пользовательского интерфейса
        val root_layout = findViewById<LinearLayout>(R.id.root_layout)
        root_layout.setBackgroundColor(primaryColor)
        val operations_layout = findViewById<LinearLayout>(R.id.operations_layout)
        operations_layout.setBackgroundColor(secondaryColor)
        setTextColor(root_layout, textColor)
        if (!isDarkTheme) {
            setBackgroundColor(root_layout, resources.getColor(R.color.white_theme_numbers))
        }
    }

    private fun setTextColor(viewGroup: ViewGroup, textColor: Int) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val childView = viewGroup.getChildAt(i)
            if (childView is TextView) {
                childView.setTextColor(textColor)
            } else if (childView is ViewGroup) {
                setTextColor(childView, textColor) // Рекурсивный вызов для дочернего ViewGroup

            }
        }
    }

    private fun setBackgroundColor(viewGroup: ViewGroup, backgroundColor: Int) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val childView = viewGroup.getChildAt(i)
            if (childView is TextView) {
                if(childView.id == R.id.b_one || childView.id == R.id.b_two || childView.id == R.id.b_three || childView.id == R.id.b_four || childView.id == R.id.b_fife || childView.id == R.id.b_six || childView.id == R.id.b_seven || childView.id == R.id.b_eight || childView.id == R.id.b_nine || childView.id == R.id.b_zero || childView.id == R.id.b_zeros || childView.id == R.id.b_point || childView.id == R.id.b_equal)
                {
                    if(childView.id == R.id.b_equal){
                    }else{
                        childView.setBackgroundColor(backgroundColor)
                    }
                }
                childView.setBackgroundColor(resources.getColor(R.color.white_theme_sci))
            } else if (childView is ViewGroup) {
                setTextColor(childView, backgroundColor) // Рекурсивный вызов для дочернего ViewGroup

            }
        }
    }

    fun isDarkThemeEnabled(context: Context): Boolean {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            val res: TextView = findViewById<TextView>(R.id.result)
            val op: TextView = findViewById<TextView>(R.id.operation)

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            currentAcceleration = sqrt((x * x + y * y).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.8f + delta

            if (acceleration > 8) {
                result.text = ""
                operation.text = ""
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        themeSwitch = findViewById(R.id.themeSwitch)

        historyRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    history_counter = document.data?.size ?: 0
                }
            } else {
                Log.e("Firestore", "Error getting document", task.exception)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        themeRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    var isDarkTheme = document.getBoolean("isDarkTheme")
                    if (isDarkTheme != null) {
                        setThemeColors(isDarkTheme)
                        themeSwitch.isChecked = isDarkTheme
                    }else{
                        isDarkTheme = isDarkThemeEnabled(this);
                        setThemeColors(isDarkTheme)
                    }

                }
            } else {
                Log.e("Firestore", "Error getting isDarkTheme", task.exception)
            }
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isDarkTheme = isChecked
            setThemeColors(isChecked)
        }

        result = findViewById<TextView>(R.id.result)
        operation = findViewById<TextView>(R.id.operation)

        val button = findViewById<Button>(R.id.history)

// Привяжите листенер к кнопке
        val passKey = intent.getBooleanExtra("PASS_KEY", false)
        if (passKey) {
            button.setText("History")
            button.setOnClickListener {
                historyRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val operationsData = documentSnapshot.data
                            // Перебор всех записей
                            if (operationsData != null) {
                                for ((key, value) in operationsData) {
                                    if (key.toString() == (history_counter - 1).toString()){
                                        operation.text = value.toString()
                                    }
                                }
                            }
                        } else {
                            Log.e("Firestore", "Error getting document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error")
                    }
            }
        }else{
            button.setText("Authorize")
            button.setOnClickListener {
                val intent = Intent(this, PassKeySetupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


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
            historyRef.update(
                hashMapOf(
                    history_counter.toString() to optext.toString(),
                    // Другие поля вашей записи истории
                ) as Map<String, String>
            )
                .addOnSuccessListener {
                    // Успешно добавлено новое поле
                }
                .addOnFailureListener { e ->
                    // Обработка ошибки
                }
            history_counter++
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

    override fun onResume() {
        sensorManager?.registerListener(
            sensorListener, sensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }
    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    override fun onStop() {
        sensorManager!!.unregisterListener(sensorListener)
        themeRef
            .update("isDarkTheme", isDarkTheme)
            .addOnSuccessListener { Log.d("Firestore", "isDarkTheme updated successfully") }
            .addOnFailureListener { e ->
                Log.e(
                    "Firestore",
                    "Error updating isDarkTheme",
                    e
                )
            }
        super.onStop()
    }
}
