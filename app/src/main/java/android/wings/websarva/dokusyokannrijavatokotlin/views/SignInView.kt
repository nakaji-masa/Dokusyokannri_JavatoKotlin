package android.wings.websarva.dokusyokannrijavatokotlin.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import android.wings.websarva.dokusyokannrijavatokotlin.R
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.sign_in_layout.view.*

class SignInView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) :
    ConstraintLayout(context, attr, defStyle) {

    private var listener: OnClickListener? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.sign_in_layout, this)

        val attrArray = context.obtainStyledAttributes(attr, R.styleable.SignInView)

        val textView = attrArray.getText(R.styleable.SignInView_set_text)
        val imageView = attrArray.getDrawable(R.styleable.SignInView_set_image)
        val background = attrArray.getDrawable(R.styleable.SignInView_background)
        val textColor = attrArray.getColor(
            R.styleable.SignInView_textColor,
            ContextCompat.getColor(context, R.color.white)
        )

        val signInViewText = view.findViewById<TextView>(R.id.signInText)
        val signInViewImage = view.findViewById<ImageView>(R.id.signInImage)
        val layout = view.findViewById<ConstraintLayout>(R.id.signInViewLayout)

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