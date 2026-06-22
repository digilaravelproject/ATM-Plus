package com.atmplus.ui

import android.view.View
import android.widget.Button
import android.widget.EditText
import com.atmplus.R

object KeyboardHelper {

    fun bindSingleEditText(keyboardView: View, editText: EditText) {
        val keys = mapOf(
            R.id.btn_key_0 to "0",
            R.id.btn_key_1 to "1",
            R.id.btn_key_2 to "2",
            R.id.btn_key_3 to "3",
            R.id.btn_key_4 to "4",
            R.id.btn_key_5 to "5",
            R.id.btn_key_6 to "6",
            R.id.btn_key_7 to "7",
            R.id.btn_key_8 to "8",
            R.id.btn_key_9 to "9"
        )

        for ((id, value) in keys) {
            keyboardView.findViewById<Button>(id)?.setOnClickListener {
                val currentText = editText.text.toString()
                val maxLength = getMaxLength(editText)
                if (maxLength == null || currentText.length < maxLength) {
                    editText.append(value)
                }
            }
        }

        keyboardView.findViewById<Button>(R.id.btn_key_del)?.setOnClickListener {
            val currentText = editText.text.toString()
            if (currentText.isNotEmpty()) {
                editText.setText(currentText.substring(0, currentText.length - 1))
                editText.setSelection(editText.text.length)
            }
        }

        keyboardView.findViewById<Button>(R.id.btn_key_clear)?.setOnClickListener {
            editText.setText("")
        }
    }

    fun bindOtpEditTexts(keyboardView: View, editTexts: Array<EditText>) {
        val keys = mapOf(
            R.id.btn_key_0 to "0",
            R.id.btn_key_1 to "1",
            R.id.btn_key_2 to "2",
            R.id.btn_key_3 to "3",
            R.id.btn_key_4 to "4",
            R.id.btn_key_5 to "5",
            R.id.btn_key_6 to "6",
            R.id.btn_key_7 to "7",
            R.id.btn_key_8 to "8",
            R.id.btn_key_9 to "9"
        )

        for ((id, value) in keys) {
            keyboardView.findViewById<Button>(id)?.setOnClickListener {
                // Find the first empty box, or the currently focused box
                val focused = editTexts.firstOrNull { it.isFocused } ?: editTexts.firstOrNull { it.text.isEmpty() } ?: editTexts.last()
                focused.setText(value)
                val index = editTexts.indexOf(focused)
                if (index < editTexts.size - 1) {
                    editTexts[index + 1].requestFocus()
                }
            }
        }

        keyboardView.findViewById<Button>(R.id.btn_key_del)?.setOnClickListener {
            val focused = editTexts.lastOrNull { it.isFocused } ?: editTexts.lastOrNull { it.text.isNotEmpty() } ?: editTexts.first()
            val index = editTexts.indexOf(focused)
            if (focused.text.isNotEmpty()) {
                focused.setText("")
            } else if (index > 0) {
                editTexts[index - 1].setText("")
                editTexts[index - 1].requestFocus()
            }
        }

        keyboardView.findViewById<Button>(R.id.btn_key_clear)?.setOnClickListener {
            for (et in editTexts) {
                et.setText("")
            }
            editTexts.first().requestFocus()
        }
    }

    private fun getMaxLength(editText: EditText): Int? {
        val filters = editText.filters
        for (filter in filters) {
            if (filter is android.text.InputFilter.LengthFilter) {
                return filter.max
            }
        }
        return null
    }
}
