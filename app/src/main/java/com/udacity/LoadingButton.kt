package com.udacity

import android.animation.PropertyValuesHolder.ofFloat
import android.animation.ValueAnimator
import android.animation.ValueAnimator.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    // Initialize variables and objects
    private var widthSize = 0
    private var heightSize = 0
    private val valueAnimator = ValueAnimator()
    private var btnBackColor: Int
    private var textColor: Int
    private var textSize: Int
    private var progress: Double = 0.0
    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        // Observe changes to button state and update view accordingly
        when (new) {
            ButtonState.Clicked -> invalidate()
            ButtonState.Completed -> {
                valueAnimator.cancel()
                invalidate()
            }
            ButtonState.Loading -> {
                animatedProgress()
                invalidate()
            }
        }
    }

    // Initialize button attributes from XML
    init {
        // Set default values
        isEnabled = true
        btnBackColor = ContextCompat.getColor(context, R.color.colorPrimary)
        textColor = ContextCompat.getColor(context, R.color.white)
        textSize = resources.getDimensionPixelSize(R.dimen.default_text_size)

        // Read values from XML if provided
        val attrs = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0)
        btnBackColor = attrs.getColor(R.styleable.LoadingButton_BackgroundColorBtn, btnBackColor)
        textColor = attrs.getColor(R.styleable.LoadingButton_TextColorBtn, textColor)
        textSize = attrs.getDimensionPixelSize(R.styleable.LoadingButton_TextSizeBtn, textSize)
    }

    // Initialize Paint objects
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val animatedRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.animated_rect)
    }

    private val animatedCirclePaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = context.getColor(R.color.colorAccent)
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 30f
    }

    // Initialize RectF object for loading circle
    private val loadingCircle = RectF(
        760f, 60f, 800f, 100f
    )

    // Draw the button view
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the button background
        paint.color = btnBackColor
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        // If button is in Loading state, draw the animated rectangle and circle
        if (buttonState == ButtonState.Loading) {
            // Draw the animated rectangle
            paint.color = Color.GRAY
            canvas.drawRect(
                0f, 0f, (width * (progress / 100)).toFloat(), height.toFloat(), animatedRectPaint
            )

            // Draw the animated circle
            paint.color = Color.GREEN
            paint.strokeWidth = 40f
            canvas.drawArc(
                loadingCircle, 0f, (360 * progress / 100).toFloat(), true, animatedCirclePaint
            )
        }

        // Draw the button text
        paint.color = textColor
        val btnText =
            if (buttonState == ButtonState.Loading) resources.getString(R.string.button_loading) else resources.getString(
                R.string.button_name
            )
        canvas.drawText(btnText, (width / 2).toFloat(), (height / 2).toFloat(), paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w), heightMeasureSpec, 0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun updateBtnState(state: ButtonState) {
        buttonState = state
    }

    private fun animatedProgress() {
        val valuesRange = ofFloat("progress", 0f, 100f)
        valueAnimator.apply {
            setValues(valuesRange)
            duration = 3000
            repeatCount = INFINITE
            repeatMode = REVERSE
            addUpdateListener {
                progress = (it.animatedValue as Float).toDouble()
                invalidate()
            }
        }
        valueAnimator.start()
    }
}
