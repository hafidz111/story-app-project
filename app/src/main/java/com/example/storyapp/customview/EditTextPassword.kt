package com.example.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class EditTextPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        hint = context.getString(R.string.enter_your_password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputLayout = findTextInputLayout()
                if (s.toString().length < 8) {
                    inputLayout?.error = context.getString(R.string.error_invalid_password)
                } else {
                    inputLayout?.error = null
                }
            }
        })
    }

    private fun findTextInputLayout(): TextInputLayout? {
        var parent = parent
        while (parent != null && parent !is TextInputLayout) {
            parent = (parent as? View)?.parent
        }
        return parent as? TextInputLayout
    }
}