package com.taqiyuddin.storyappdicoding.submissionakhir.view.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class EmailEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        setupTextWatcher()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupTextWatcher()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupTextWatcher()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = "Masukkan email"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun setupTextWatcher() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                val inputText = charSequence.toString()
                error = if (!inputText.matches(emailRegex.toRegex())) {
                    "Must Include @"
                } else {
                    null
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }
}