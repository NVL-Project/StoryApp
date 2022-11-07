package com.nvl.storyapp.custome

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nvl.storyapp.R

class CustomEmailEditText : AppCompatEditText{
    private lateinit var emailIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        emailIconDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        compoundDrawablePadding = 16

        setHint(R.string.email)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)
        }
        setDrawables(emailIconDrawable)

        setHint(R.string.email)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches())
                    error =context.getString(R.string.error_email)
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
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