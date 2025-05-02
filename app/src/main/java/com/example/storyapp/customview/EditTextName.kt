package com.example.storyapp.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputLayout

class EditTextName @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        hint = context.getString(R.string.enter_your_name)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputLayout = findTextInputLayout()
                if (s.isNullOrBlank()) {
                    inputLayout?.error = context.getString(R.string.error_empty_name)
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