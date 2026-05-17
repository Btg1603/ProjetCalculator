package com.ptithcm.projectcalculater

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ptithcm.projectcalculater.databinding.ActivityMainBinding
import com.ptithcm.projectcalculater.viewmodel.CalculatorViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.expression.observe(this) { binding.tvExpression.text = it }
        viewModel.result.observe(this)     { binding.tvResult.text     = it }

        viewModel.isShiftActive.observe(this) { active ->
            binding.indicatorShift.visibility = if (active) View.VISIBLE else View.INVISIBLE
        }
        viewModel.isAlphaActive.observe(this) { active ->
            binding.indicatorAlpha.visibility = if (active) View.VISIBLE else View.INVISIBLE
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {

        // System
        binding.btnOn.setOnClickListener       { viewModel.clearAll() }
        binding.btnShift.setOnClickListener    { viewModel.toggleShift() }
        binding.btnHome.setOnClickListener     { viewModel.clearAll() }
        binding.btnBack.setOnClickListener     { viewModel.deleteLast() }
        binding.btnSettings.setOnClickListener { }

        // D-Pad
        binding.btnOk.setOnClickListener         { viewModel.calculate() }
        binding.btnLeft.setOnClickListener       { viewModel.moveCursorLeft() }
        binding.btnRight.setOnClickListener      { viewModel.moveCursorRight() }
        binding.btnUp.setOnClickListener         { viewModel.historyPrev() }
        binding.btnDown.setOnClickListener       { viewModel.historyNext() }
        binding.btnScrollUp.setOnClickListener   { viewModel.scrollUp() }
        binding.btnScrollDown.setOnClickListener { viewModel.scrollDown() }

        // Function strip
        binding.btnVariable.setOnClickListener { viewModel.append("x") }
        binding.btnFunction.setOnClickListener { }
        binding.btnCatalog.setOnClickListener  { }
        binding.btnTools.setOnClickListener    { }

        // Numbers
        mapOf(
            binding.btn0 to "0", binding.btn1 to "1", binding.btn2 to "2",
            binding.btn3 to "3", binding.btn4 to "4", binding.btn5 to "5",
            binding.btn6 to "6", binding.btn7 to "7", binding.btn8 to "8",
            binding.btn9 to "9", binding.btnDot to "."
        ).forEach { (btn, v) -> btn.setOnClickListener { viewModel.append(v) } }

        // Operators
        binding.btnAdd.setOnClickListener { viewModel.append("+") }
        binding.btnSub.setOnClickListener { viewModel.append("-") }
        binding.btnMul.setOnClickListener { viewModel.append("×") }
        binding.btnDiv.setOnClickListener { viewModel.append("÷") }

        // Brackets
        binding.btnOpenBracket.setOnClickListener { viewModel.append("(") }

        // Trig
        binding.btnSin.setOnClickListener  { viewModel.append("sin(") }
        binding.btnCos.setOnClickListener  { viewModel.append("cos(") }
        binding.btnTan.setOnClickListener  { viewModel.append("tan(") }
        binding.btnAsin.setOnClickListener { viewModel.append("asin(") }
        binding.btnAcos.setOnClickListener { viewModel.append("acos(") }
        binding.btnAtan.setOnClickListener { viewModel.append("atan(") }

        // Log / constants
        binding.btnLog.setOnClickListener { viewModel.append("log(") }
        binding.btnLn.setOnClickListener  { viewModel.append("ln(") }
        binding.btnPi.setOnClickListener  { viewModel.append("π") }

        // Roots / power
        binding.btnSqrt.setOnClickListener       { viewModel.append("√(") }
        binding.btnCbrt.setOnClickListener       { viewModel.append("∛(") }
        binding.btnReciprocal.setOnClickListener { viewModel.append("^(-1)") }
        binding.btnInv.setOnClickListener        { viewModel.append("^(-1)") }

        // Factorial
        binding.btnFactorial.setOnClickListener { viewModel.append("!") }

        // Scientific notation
        binding.btnExpConst.setOnClickListener { viewModel.append("E") }

        // Delete / execute
        binding.btnDel.setOnClickListener    { viewModel.deleteLast() }
        binding.btnAc.setOnClickListener     { viewModel.clearAll() }
        binding.btnExe.setOnClickListener    { viewModel.calculate() }
        binding.btnFormat.setOnClickListener { viewModel.toggleFormat() }

        // Row 8
        binding.btnOff.setOnClickListener { finish() }
        binding.btnIns.setOnClickListener { }
    }
}