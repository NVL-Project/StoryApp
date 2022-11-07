package com.nvl.storyapp.custome

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nvl.storyapp.R

class CustomPasswordEditText : AppCompatEditText{
    private lateinit var passwordIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun init() {
        passwordIconDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        compoundDrawablePadding = 16

        setHint(R.string.password)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_PASSWORD)
        }
        setDrawables(passwordIconDrawable)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty() && s.length < 6)
                    error =context. getString(R.string.password_limite)
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }
    private fun setDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}