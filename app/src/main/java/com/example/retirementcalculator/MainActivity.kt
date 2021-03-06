package com.example.retirementcalculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlin.math.pow


class MainActivity : AppCompatActivity() {

    private lateinit var calculateButton: Button
    private lateinit var interestEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var retirementEditText: EditText
    private lateinit var monthlySavingsEditText: EditText
    private lateinit var currentEditText: EditText
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calculateButton = findViewById(R.id.calculateButton)
        interestEditText = findViewById(R.id.interestEditText)
        ageEditText = findViewById(R.id.ageEditText)
        retirementEditText = findViewById(R.id.retirementEditText)
        monthlySavingsEditText = findViewById(R.id.monthlySavingsEditText)
        currentEditText = findViewById(R.id.currentEditText)
        resultTextView = findViewById(R.id.resultTextView)
//9d8c01d8-b818-408c-bb63-8ce105dc90e0
//fb555114-a0ee-4e85-89fe-74ccc8583f60
        AppCenter.start(
            application, "9d8c01d8-b818-408c-bb63-8ce105dc90e0",
            Analytics::class.java, Crashes::class.java
        )

        calculateButton.setOnClickListener {
            //Crashes.generateTestCrash()
            try {
                val interestRate = interestEditText.text.toString().toFloat()
                val currentAge = ageEditText.text.toString().toInt()
                val retirementAge = retirementEditText.text.toString().toInt()
                val monthly = monthlySavingsEditText.text.toString().toFloat()
                val current = currentEditText.text.toString().toFloat()

                val properties: HashMap<String, String> = HashMap()
                properties["interest_rate"] = interestRate.toString()
                properties["current_age"] = currentAge.toString()
                properties["retirement_age"] = retirementAge.toString()
                properties["monthly_savings"] = monthly.toString()
                properties["current_savings"] = current.toString()

                if (interestRate <= 0) {
                    Analytics.trackEvent("wrong_interest_rate")
                }
                if (retirementAge <= currentAge) {
                    Analytics.trackEvent("wrong_age")
                }

                val futureSavings = calculateRetirement(interestRate, current, monthly, (retirementAge - currentAge)*12)

                resultTextView.text =
                    "At the current rate of $interestRate%, saving \$$monthly a month you will have \$${
                        String.format(
                            "%f",
                            futureSavings
                        )
                    } by $retirementAge."

            } catch (ex: Exception) {
                Analytics.trackEvent(ex.message)
            }


        }

    }
    private fun calculateRetirement(interestRate: Float, currentSavings: Float, monthly: Float, numMonths: Int): Float {
        var futureSavings = currentSavings * (1+(interestRate/100/12)).pow(numMonths)

        for (i in 1..numMonths) {
            futureSavings += monthly * (1+(interestRate/100/12)).pow(i)
        }

        return  futureSavings
    }
}
