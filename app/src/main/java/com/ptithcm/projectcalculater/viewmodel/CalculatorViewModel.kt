package com.ptithcm.projectcalculater.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.*

class CalculatorViewModel : ViewModel() {

    // ── LiveData ──────────────────────────────────────────────────────────────
    private val _expression    = MutableLiveData("")
    val expression: LiveData<String> = _expression

    private val _result        = MutableLiveData("")
    val result: LiveData<String> = _result

    private val _isShiftActive = MutableLiveData(false)
    val isShiftActive: LiveData<Boolean> = _isShiftActive

    private val _isAlphaActive = MutableLiveData(false)
    val isAlphaActive: LiveData<Boolean> = _isAlphaActive

    // ── State ─────────────────────────────────────────────────────────────────
    private val exprBuffer   = StringBuilder()   // nội dung biểu thức hiện tại
    private var cursorPos    = 0                 // vị trí con trỏ (index trong buffer)
    private var lastAnswer   = "0"              // giá trị Ans
    private val history      = mutableListOf<String>() // lịch sử biểu thức
    private var historyIndex = -1               // con trỏ lịch sử
    private var useDecimal   = true             // FORMAT toggle

    // ── Input ─────────────────────────────────────────────────────────────────

    /** Chèn chuỗi tại vị trí con trỏ */
    fun append(value: String) {
        exprBuffer.insert(cursorPos, value)
        cursorPos += value.length
        publishExpression()
        evaluateLive()
    }

    /** Xoá ký tự trước con trỏ */
    fun deleteLast() {
        if (cursorPos > 0) {
            exprBuffer.deleteCharAt(cursorPos - 1)
            cursorPos--
            publishExpression()
            evaluateLive()
        }
    }

    /** Xoá toàn bộ */
    fun clearAll() {
        exprBuffer.clear()
        cursorPos = 0
        _expression.value = ""
        _result.value     = ""
        historyIndex      = -1
    }

    // ── Cursor ────────────────────────────────────────────────────────────────
    fun moveCursorLeft()  { if (cursorPos > 0)                  cursorPos-- }
    fun moveCursorRight() { if (cursorPos < exprBuffer.length)  cursorPos++ }

    // ── History ───────────────────────────────────────────────────────────────
    fun historyPrev() {
        if (history.isEmpty()) return
        if (historyIndex < history.size - 1) historyIndex++
        loadHistory()
    }

    fun historyNext() {
        if (historyIndex > 0) { historyIndex--; loadHistory() }
        else { historyIndex = -1; clearAll() }
    }

    private fun loadHistory() {
        if (historyIndex < 0 || historyIndex >= history.size) return
        val expr = history[history.size - 1 - historyIndex]
        exprBuffer.clear()
        exprBuffer.append(expr)
        cursorPos = exprBuffer.length
        publishExpression()
        evaluateLive()
    }

    // ── Scroll (stub – UI sẽ tự handle nếu dùng HorizontalScrollView) ────────
    fun scrollUp()   { /* handled by UI scroll */ }
    fun scrollDown() { /* handled by UI scroll */ }

    // ── Shift / Alpha ─────────────────────────────────────────────────────────
    fun toggleShift() {
        _isShiftActive.value = !(_isShiftActive.value ?: false)
    }

    fun toggleAlpha() {
        _isAlphaActive.value = !(_isAlphaActive.value ?: false)
    }

    // ── Format (thập phân ↔ phân số / S-D) ───────────────────────────────────
    fun toggleFormat() {
        useDecimal = !useDecimal
        // Tính lại và hiển thị lại với định dạng mới
        val current = _result.value ?: return
        val num = current.toDoubleOrNull() ?: return
        _result.value = formatNumber(num)
    }

    // ── Calculate ─────────────────────────────────────────────────────────────
    fun calculate() {
        val raw = exprBuffer.toString().trim()
        if (raw.isEmpty()) return

        try {
            val value = eval(raw)
            lastAnswer = formatNumber(value)
            _result.value = lastAnswer

            // Lưu lịch sử
            if (history.isEmpty() || history.last() != raw) history.add(raw)
            historyIndex = -1
        } catch (e: Exception) {
            _result.value = "Math ERROR"
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun publishExpression() {
        _expression.value = exprBuffer.toString()
    }

    /** Tính nhanh để hiển thị preview trong lúc nhập */
    private fun evaluateLive() {
        val raw = exprBuffer.toString().trim()
        if (raw.isEmpty()) { _result.value = ""; return }
        try {
            val value = eval(raw)
            _result.value = formatNumber(value)
        } catch (_: Exception) {
            _result.value = ""   // không hiển thị lỗi khi đang nhập
        }
    }

    private fun formatNumber(value: Double): String {
        if (value.isNaN()) return "Math ERROR"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
        return if (value == kotlin.math.floor(value) && kotlin.math.abs(value) < 1e15)
            value.toLong().toString()
        else
            value.toBigDecimal().stripTrailingZeros().toPlainString()
    }

    // ── Expression Evaluator ──────────────────────────────────────────────────

    /**
     * Trình đánh giá biểu thức đệ quy đơn giản.
     * Hỗ trợ: +  −  ×  ÷  ^  ()  sin cos tan log ln √ ∛ !
     * Hỗ trợ: Ans  E (×10^n)  pi (π)  e
     */
    private fun eval(expr: String): Double {
        val src = expr
            .replace("Ans", lastAnswer)
            .replace("π", "pi")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
        return Parser(src).parse()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Recursive Descent Parser
    // ─────────────────────────────────────────────────────────────────────────

    private inner class Parser(private val input: String) {
        private var pos = 0

        fun parse(): Double {
            val result = parseExpr()
            if (pos < input.length) throw RuntimeException("Unexpected: ${input[pos]}")
            return result
        }

        // expr → term (('+' | '-') term)*
        private fun parseExpr(): Double {
            var result = parseTerm()
            while (pos < input.length) {
                when {
                    eat('+') -> result += parseTerm()
                    eat('-') -> result -= parseTerm()
                    else     -> break
                }
            }
            return result
        }

        // term → factor (('*' | '/') factor)*
        private fun parseTerm(): Double {
            var result = parseUnary()
            while (pos < input.length) {
                when {
                    eat('*') -> result *= parseUnary()
                    eat('/') -> {
                        val d = parseUnary()
                        if (d == 0.0) throw ArithmeticException("Division by zero")
                        result /= d
                    }
                    else -> break
                }
            }
            return result
        }

        // unary → '-' factor | factor
        private fun parseUnary(): Double {
            if (eat('-')) return -parsePower()
            if (eat('+')) return  parsePower()
            return parsePower()
        }

        // power → postfix ('^' unary)?
        private fun parsePower(): Double {
            var base = parsePostfix()
            if (eat('^')) base = base.pow(parseUnary())
            return base
        }

        // postfix → primary ('!')?
        private fun parsePostfix(): Double {
            var v = parsePrimary()
            while (eat('!')) v = factorial(v)
            return v
        }

        // primary → number | func '(' expr ')' | '(' expr ')' | constant
        private fun parsePrimary(): Double {
            skipSpaces()

            // Parenthesised group
            if (eat('(')) {
                val v = parseExpr()
                eat(')')
                return v
            }

            // Named functions / constants
            val func = tryReadIdent()
            if (func != null) {
                return when (func.lowercase()) {
                    "sin"  -> { val a = parenArg(); sin(toRad(a)) }
                    "cos"  -> { val a = parenArg(); cos(toRad(a)) }
                    "tan"  -> { val a = parenArg(); tan(toRad(a)) }
                    "asin", "sin⁻¹" -> { val a = parenArg(); Math.toDegrees(asin(a)) }
                    "acos", "cos⁻¹" -> { val a = parenArg(); Math.toDegrees(acos(a)) }
                    "atan", "tan⁻¹" -> { val a = parenArg(); Math.toDegrees(atan(a)) }
                    "log"  -> { val a = parenArg(); log10(a) }
                    "ln"   -> { val a = parenArg(); ln(a) }
                    "sqrt", "√" -> { val a = parenArg(); sqrt(a) }
                    "cbrt", "∛" -> { val a = parenArg(); cbrt(a) }
                    "abs"  -> { val a = parenArg(); abs(a) }
                    "pi"   -> Math.PI
                    "e"    -> Math.E
                    else   -> throw RuntimeException("Unknown function: $func")
                }
            }

            // √ / ∛ without word boundary
            if (eat('√')) { eat('('); val a = parseExpr(); eat(')'); return sqrt(a) }
            if (eat('∛')) { eat('('); val a = parseExpr(); eat(')'); return cbrt(a) }

            // Number (including scientific notation with 'E')
            return readNumber()
        }

        // ── Helpers ───────────────────────────────────────────────────────────

        private fun parenArg(): Double {
            skipSpaces()
            if (!eat('(')) throw RuntimeException("Expected '('")
            val v = parseExpr()
            eat(')')
            return v
        }

        private fun tryReadIdent(): String? {
            skipSpaces()
            if (pos >= input.length || !input[pos].isLetter()) return null
            val start = pos
            while (pos < input.length && (input[pos].isLetterOrDigit() || input[pos] == '⁻' || input[pos] == '¹')) pos++
            return input.substring(start, pos)
        }

        private fun readNumber(): Double {
            skipSpaces()
            val start = pos
            if (pos < input.length && input[pos] == '-') pos++   // leading minus (already handled by unary, but safety)
            while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
            // Scientific notation: 1.5E10 or 1.5e-10
            if (pos < input.length && input[pos].uppercaseChar() == 'E') {
                pos++
                if (pos < input.length && (input[pos] == '+' || input[pos] == '-')) pos++
                while (pos < input.length && input[pos].isDigit()) pos++
            }
            val token = input.substring(start, pos)
            return token.toDoubleOrNull() ?: throw RuntimeException("Bad number: '$token'")
        }

        private fun eat(c: Char): Boolean {
            skipSpaces()
            return if (pos < input.length && input[pos] == c) { pos++; true } else false
        }

        private fun skipSpaces() { while (pos < input.length && input[pos] == ' ') pos++ }

        private fun toRad(deg: Double) = Math.toRadians(deg)

        private fun factorial(n: Double): Double {
            val k = n.toLong()
            if (k < 0) throw ArithmeticException("Factorial of negative")
            if (k > 20) throw ArithmeticException("Factorial too large")
            var r = 1L
            for (i in 2..k) r *= i
            return r.toDouble()
        }
    }
}