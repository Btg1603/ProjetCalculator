package com.ptithcm.projectcalculater.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.PI

class CalculatorViewModel : ViewModel() {

    private val _expression = MutableLiveData("")
    val expression: LiveData<String> = _expression

    private val _result = MutableLiveData("0")
    val result: LiveData<String> = _result

    private val _isShiftActive = MutableLiveData(false)
    val isShiftActive: LiveData<Boolean> = _isShiftActive

    private val _isAlphaActive = MutableLiveData(false)
    val isAlphaActive: LiveData<Boolean> = _isAlphaActive

    private var lastAnswer = "0"

    fun append(value: String) {
        _expression.value = (_expression.value ?: "") + value
    }

    fun clearAll() {
        _expression.value = ""
        _result.value = "0"
    }

    fun deleteLast() {
        val current = _expression.value ?: ""

        if (current.isNotEmpty()) {
            _expression.value = current.dropLast(1)
        }
    }

    fun toggleShift() {
        _isShiftActive.value = !(_isShiftActive.value ?: false)

        if (_isShiftActive.value == true) {
            _isAlphaActive.value = false
        }
    }

    fun toggleAlpha() {
        _isAlphaActive.value = !(_isAlphaActive.value ?: false)

        if (_isAlphaActive.value == true) {
            _isShiftActive.value = false
        }
    }

    fun calculate() {

        try {

            var expr = _expression.value ?: ""

            expr = expr
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace("π", PI.toString())
                .replace("Ans", lastAnswer)
                .replace("√", "sqrt")
                .replace("sin(", "sin(")
                .replace("cos(", "cos(")
                .replace("tan(", "tan(")
                .replace("log(", "log10(")
                .replace("ln(", "log(")

            val result = ExpressionBuilder(expr)
                .build()
                .evaluate()

            val finalResult =
                if (result % 1 == 0.0) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }

            lastAnswer = finalResult

            _result.value = finalResult

        } catch (e: Exception) {

            _result.value = "Math Error"
        }
    }
}