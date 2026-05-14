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

        viewModel.expression.observe(this) {
            binding.tvExpression.text = it
        }
        viewModel.result.observe(this) {
            binding.tvResult.text = it
        }

        viewModel.isShiftActive.observe(this) { active ->
            binding.indicatorShift.visibility = if (active) View.VISIBLE else View.INVISIBLE
        }
        viewModel.isAlphaActive.observe(this) { active ->
            binding.indicatorAlpha.visibility = if (active) View.VISIBLE else View.INVISIBLE
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.btnShift.setOnClickListener {
            viewModel.toggleShift()
        }

        val numberButtons = mapOf(
            binding.btn0 to "0",
            binding.btn1 to "1",
            binding.btn2 to "2",
            binding.btn3 to "3",
            binding.btn4 to "4",
            binding.btn5 to "5",
            binding.btn6 to "6",
            binding.btn7 to "7",
            binding.btn8 to "8",
            binding.btn9 to "9",
            binding.btnDot to "."
        )

        numberButtons.forEach { (btn, value) ->
            btn.setOnClickListener {
                viewModel.append(value)
            }
        }

        binding.btnKeypadX.setOnClickListener {
            viewModel.append("x")
        }

        binding.btnVariable.setOnClickListener {
            viewModel.append("x")
        }

        binding.btnAdd.setOnClickListener {
            viewModel.append("+")
        }

        binding.btnSub.setOnClickListener {
            viewModel.append("-")
        }

        binding.btnMul.setOnClickListener {
            viewModel.append("×")
        }

        binding.btnDiv.setOnClickListener {
            viewModel.append("÷")
        }

        binding.btnOpenBracket.setOnClickListener {
            viewModel.append("(")
        }

        binding.btnCloseBracket.setOnClickListener {
            viewModel.append(")")
        }

        binding.btnPow.setOnClickListener {
            viewModel.append("^")
        }

        binding.btnSquare.setOnClickListener {
            viewModel.append("^2")
        }

        binding.btnSqrt.setOnClickListener {
            viewModel.append("√(")
        }

        binding.btnSin.setOnClickListener {
            viewModel.append("sin(")
        }

        binding.btnCos.setOnClickListener {
            viewModel.append("cos(")
        }

        binding.btnTan.setOnClickListener {
            viewModel.append("tan(")
        }

        binding.btnLog.setOnClickListener {
            viewModel.append("log(")
        }

        binding.btnLn.setOnClickListener {
            viewModel.append("ln(")
        }

        binding.btnAns.setOnClickListener {
            viewModel.append("Ans")
        }

        binding.btnExpConst.setOnClickListener {
            viewModel.append("E")
        }

        binding.btnComma.setOnClickListener {
            viewModel.append(",")
        }

        binding.btnAc.setOnClickListener {
            viewModel.clearAll()
        }

        binding.btnDel.setOnClickListener {
            viewModel.deleteLast()
        }

        binding.btnExe.setOnClickListener {
            viewModel.calculate()
        }
    }
}
