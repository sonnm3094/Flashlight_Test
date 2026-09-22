package com.af.pb.component.screenlight.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ColorSatValView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var hue: Float = 0f
    private var sat: Float = 1f
    private var value: Float = 1f

    private val valPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f * resources.displayMetrics.density
        color = Color.WHITE
        setShadowLayer(4f * resources.displayMetrics.density, 0f, 0f, Color.parseColor("#80000000"))
    }
    private val thumbInnerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f * resources.displayMetrics.density
        color = Color.parseColor("#40000000")
    }

    private val cornerRadius = 16f * resources.displayMetrics.density
    private val thumbRadius = 10f * resources.displayMetrics.density
    private val clipPath = Path()
    private val rectF = RectF()

    private var onColorChangedListener: ((Float, Float) -> Unit)? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setOnColorChangedListener(listener: (sat: Float, value: Float) -> Unit) {
        onColorChangedListener = listener
    }

    fun setHue(hue: Float) {
        this.hue = hue.coerceIn(0f, 360f)
        updateShader()
        invalidate()
    }

    fun setSatVal(sat: Float, value: Float) {
        this.sat = sat.coerceIn(0f, 1f)
        this.value = value.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.set(0f, 0f, w.toFloat(), h.toFloat())
        clipPath.reset()
        clipPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
        updateShader()
    }

    private fun updateShader() {
        if (width <= 0 || height <= 0) return

        val hueColor = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))

        val satShader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            Color.WHITE, hueColor,
            Shader.TileMode.CLAMP
        )

        val valShader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            Color.TRANSPARENT, Color.BLACK,
            Shader.TileMode.CLAMP
        )

        val composeShader = ComposeShader(valShader, satShader, PorterDuff.Mode.SRC_OVER)
        valPaint.shader = composeShader
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), valPaint)
        canvas.restore()

        // Draw thumb
        val thumbX = (sat * width).coerceIn(thumbRadius, width - thumbRadius)
        val thumbY = ((1f - value) * height).coerceIn(thumbRadius, height - thumbRadius)

        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint)
        canvas.drawCircle(thumbX, thumbY, thumbRadius + thumbPaint.strokeWidth / 2f, thumbInnerPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                sat = (event.x / width).coerceIn(0f, 1f)
                value = (1f - (event.y / height)).coerceIn(0f, 1f)
                invalidate()
                onColorChangedListener?.invoke(sat, value)
                return true
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
