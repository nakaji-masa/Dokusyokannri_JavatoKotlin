package android.wings.websarva.dokusyokannrijavatokotlin.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.wings.websarva.dokusyokannrijavatokotlin.R
import android.wings.websarva.dokusyokannrijavatokotlin.databinding.SignInLayoutBinding
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class SignInView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) :
    ConstraintLayout(context, attr, defStyle) {

    private var listener: OnClickListener? = null

    init {
        val binding = SignInLayoutBinding.inflate(LayoutInflater.from(context), this, true)

        val attrArray = context.obtainStyledAttributes(attr, R.styleable.SignInView)

        val textView = attrArray.getText(R.styleable.SignInView_set_text)
        val imageView = attrArray.getDrawable(R.styleable.SignInView_set_image)
        val background = attrArray.getDrawable(R.styleable.SignInView_background)
        val textColor = attrArray.getColor(
            R.styleable.SignInView_textColor,
            ContextCompat.getColor(context, R.color.white)
        )

        val signInViewText = binding.signInText
        val signInViewImage = binding.signInImage
        val layout = binding.signInViewLayout

        layout.background = background

        signInViewText.text = textView
        signInViewText.setTextColor(textColor)

        signInViewImage.setImageDrawable(imageView)

        attrArray.recycle()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        listener = l
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_UP) {
            if (checkInside(ev)) {
                post { listener?.onClick(this@SignInView) }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun checkInside(ev: MotionEvent): Boolean {
        val point = IntArray(2)
        getLocationOnScreen(point)
        val x = point[0]
        val y = point[1]
        return ev.rawX >= x && ev.rawX <= x + width &&
                ev.rawY >= y && ev.rawY <= y + height
    }

}