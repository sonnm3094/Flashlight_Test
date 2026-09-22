package com.af.pb.component.screenlight.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
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

class ColorAlphaSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var alphaVal: Int = 255
    private var baseColor: Int = Color.WHITE

    private val checkerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
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

    private var onAlphaChangedListener: ((Int) -> Unit)? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        createCheckerPattern()
    }

    private fun createCheckerPattern() {
        val size = (6 * resources.displayMetrics.density).toInt().coerceAtLeast(4)
        val bitmap = Bitmap.createBitmap(size * 2, size * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val p1 = Paint().apply { color = Color.parseColor("#E0E0E0") }
        val p2 = Paint().apply { color = Color.WHITE }

        canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), p1)
        canvas.drawRect(size.toFloat(), 0f, (size * 2).toFloat(), size.toFloat(), p2)
        canvas.drawRect(0f, size.toFloat(), size.toFloat(), (size * 2).toFloat(), p2)
        canvas.drawRect(size.toFloat(), size.toFloat(), (size * 2).toFloat(), (size * 2).toFloat(), p1)

        checkerPaint.shader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }

    fun setOnAlphaChangedListener(listener: (Int) -> Unit) {
        onAlphaChangedListener = listener
    }

    fun setColor(color: Int) {
        this.baseColor = color
        updateGradient()
        invalidate()
    }

    fun setAlphaValue(alpha: Int) {
        this.alphaVal = alpha.coerceIn(0, 255)
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
        updateGradient()
    }

    private fun updateGradient() {
        if (barRect.width() <= 0) return
        val startColor = Color.argb(0, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
        val endColor = Color.argb(255, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))

        gradientPaint.shader = LinearGradient(
            barRect.left, 0f, barRect.right, 0f,
            startColor, endColor, Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw bar
        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawRect(barRect, checkerPaint)
        canvas.drawRect(barRect, gradientPaint)
        canvas.restore()

        // Draw thumb
        val usableWidth = width - 2 * thumbMargin
        val thumbX = thumbMargin + (alphaVal / 255f) * usableWidth
        val thumbY = height / 2f

        thumbFillPaint.color = Color.argb(alphaVal, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))
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
                    alphaVal = ((relativeX / usableWidth) * 255).toInt().coerceIn(0, 255)
                    invalidate()
                    onAlphaChangedListener?.invoke(alphaVal)
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
