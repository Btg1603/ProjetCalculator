package com.ptithcm.projectcalculater

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
        binding.btnHome.setOnClickListener     { showModeDialog() }
        binding.btnBack.setOnClickListener     { viewModel.deleteLast() }
        binding.btnSettings.setOnClickListener { 
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

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
        binding.btnFunction.setOnClickListener { showFunctionDialog() }
        binding.btnCatalog.setOnClickListener  { showCatalogDialog() }
        binding.btnTools.setOnClickListener    { showToolsDialog() }

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
        binding.btnIns.setOnClickListener { viewModel.toggleInsertMode() }
    }

    private fun showModeDialog() {
        val modes = arrayOf(
            "Tính toán",       // Calculate
            "Thống kê",        // Statistics
            "Phân phối",       // Distribution
            "Bảng",            // Table
            "Phương trình",    // Equation
            "Bất phương trình", // Inequality
            "Số phức",         // Complex Numbers
            "Ma trận",         // Matrix
            "Vectơ"            // Vector
        )
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Chế độ tính toán")
            .setItems(modes) { _, which ->
                when (which) {
                    0 -> {
                        viewModel.clearAll()
                        showToast("Tính toán - Chế độ cơ bản")
                    }
                    1 -> showToast("Thống kê - Chế độ này sẽ được phát triển")
                    2 -> showToast("Phân phối - Chế độ này sẽ được phát triển")
                    3 -> showToast("Bảng - Chế độ này sẽ được phát triển")
                    4 -> showToast("Phương trình - Chế độ này sẽ được phát triển")
                    5 -> showToast("Bất phương trình - Chế độ này sẽ được phát triển")
                    6 -> showToast("Số phức - Chế độ này sẽ được phát triển")
                    7 -> showToast("Ma trận - Chế độ này sẽ được phát triển")
                    8 -> showToast("Vectơ - Chế độ này sẽ được phát triển")
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showFunctionDialog() {
        val functions = arrayOf("GCD", "LCM", "MOD", "PERMUTATION", "COMBINATION")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Functions")
            .setItems(functions) { _, which ->
                when (which) {
                    0 -> viewModel.append("gcd(")
                    1 -> viewModel.append("lcm(")
                    2 -> viewModel.append("mod(")
                    3 -> viewModel.append("perm(")
                    4 -> viewModel.append("comb(")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCatalogDialog() {
        val items = arrayOf("Constants", "Units", "More Functions")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Catalog")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showConstantsDialog()
                    1 -> showUnitsDialog()
                    2 -> showMoreFunctionsDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showConstantsDialog() {
        val constants = arrayOf("Gravitational Constant (G)", "Speed of Light (c)", "Planck's Constant (h)")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Constants")
            .setItems(constants) { _, which ->
                when (which) {
                    0 -> viewModel.append("6.674E-11")
                    1 -> viewModel.append("299792458")
                    2 -> viewModel.append("6.62607015E-34")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showUnitsDialog() {
        val units = arrayOf("°C to K", "km to m", "kg to g")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Unit Conversions")
            .setItems(units) { _, which ->
                when (which) {
                    0 -> viewModel.clearAll()
                    1 -> viewModel.clearAll()
                    2 -> viewModel.clearAll()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMoreFunctionsDialog() {
        val functions = arrayOf("Random", "Round", "Ceiling", "Floor", "Absolute")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("More Functions")
            .setItems(functions) { _, which ->
                when (which) {
                    0 -> viewModel.append("rand(")
                    1 -> viewModel.append("round(")
                    2 -> viewModel.append("ceil(")
                    3 -> viewModel.append("floor(")
                    4 -> viewModel.append("abs(")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showToolsDialog() {
        val tools = arrayOf("Fraction", "Decimal Conversion", "Matrix", "Statistics")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Tools")
            .setItems(tools) { _, which ->
                when (which) {
                    0 -> viewModel.append("frac(")
                    1 -> viewModel.clearAll()
                    2 -> viewModel.clearAll()
                    3 -> viewModel.clearAll()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}