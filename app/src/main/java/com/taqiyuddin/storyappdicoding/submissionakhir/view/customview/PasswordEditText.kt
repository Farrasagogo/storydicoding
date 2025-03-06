package com.taqiyuddin.storyappdicoding.submissionakhir.view.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasswordEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        setupTextWatcher()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupTextWatcher()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupTextWatcher()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Masukkan password"
        textAlignment = TEXT_ALIGNMENT_VIEW_START
    }

    private fun setupTextWatcher() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text = s.toString().trim { it <= ' ' }
                if (text.length < 8) {
                    setError("Password minimal 8 Karakter", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }
}