package com.af.pb.component.screenlight.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ColorHueSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var hue: Float = 0f
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val thumbFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokeWidthVal = 3f * resources.displayMetrics.density
    private val shadowRadiusVal = 3f * resources.displayMetrics.density

    private val thumbStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthVal
        color = Color.WHITE
        setShadowLayer(shadowRadiusVal, 0f, 0f, Color.parseColor("#40000000"))
    }

    private val barHeight = 14f * resources.displayMetrics.density
    private val thumbRadius = 12f * resources.displayMetrics.density
    private val thumbMargin = thumbRadius + strokeWidthVal / 2f + shadowRadiusVal

    private val barRect = RectF()
    private val clipPath = Path()

    private val rainbowColors = intArrayOf(
        Color.RED,
        Color.YELLOW,
        Color.GREEN,
        Color.CYAN,
        Color.BLUE,
        Color.MAGENTA,
        Color.RED
    )

    private var onHueChangedListener: ((Float) -> Unit)? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setOnHueChangedListener(listener: (Float) -> Unit) {
        onHueChangedListener = listener
    }

    fun setHue(hue: Float) {
        this.hue = hue.coerceIn(0f, 360f)
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val barTop = (h - barHeight) / 2f
        val barBottom = barTop + barHeight
        val left = thumbMargin
        val right = w.toFloat() - thumbMargin

        barRect.set(left, barTop, right, barBottom)
        clipPath.reset()
        clipPath.addRoundRect(barRect, barHeight / 2f, barHeight / 2f, Path.Direction.CW)

        val gradient = LinearGradient(
            left, 0f, right, 0f,
            rainbowColors, null, Shader.TileMode.CLAMP
        )
        barPaint.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw rainbow bar
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawRect(barRect, barPaint)
        canvas.restore()

        // Draw thumb
        val usableWidth = width - 2 * thumbMargin
        val thumbX = thumbMargin + (hue / 360f) * usableWidth
        val thumbY = height / 2f

        thumbFillPaint.color = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbFillPaint)
        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbStrokePaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                val usableWidth = width - 2 * thumbMargin
                if (usableWidth > 0) {
                    val relativeX = (event.x - thumbMargin).coerceIn(0f, usableWidth)
                    hue = (relativeX / usableWidth) * 360f
                    invalidate()
                    onHueChangedListener?.invoke(hue)
                }
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = (thumbMargin * 2 + 8 * resources.displayMetrics.density).toInt()
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        val width = resolveSize(0, widthMeasureSpec)
        setMeasuredDimension(width, height)
    }
}
