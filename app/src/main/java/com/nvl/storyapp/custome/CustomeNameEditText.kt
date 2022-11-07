package com.nvl.storyapp.custome

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.nvl.storyapp.R

class CustomeNameEditText : AppCompatEditText{
    private lateinit var nameIconDrawable: Drawable

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
        nameIconDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_person) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        compoundDrawablePadding = 16

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_NAME)
        }
        setDrawables(nameIconDrawable)

        setHint(R.string.name)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().isEmpty())  error =context.getString(R.string.add_name)
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