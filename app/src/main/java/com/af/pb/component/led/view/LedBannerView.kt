package com.af.pb.component.led.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.af.pb.R
import com.af.pb.domain.model.LedDirection
import com.af.pb.domain.model.LedVisualEffect

class LedBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var text: String = "HELLO WORLD"
    private var fontSizePx: Float = 64f
    private var scrollSpeedSec: Int = 5
    private var textColor: Int = 0xFFFFD54F.toInt()
    private var direction: LedDirection = LedDirection.LEFT
    private var visualEffect: LedVisualEffect = LedVisualEffect.GLOW
    private var backgroundResId: Int? = null
    private var backgroundBitmap: Bitmap? = null

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }
    private val neonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textBounds = Rect()

    private var offsetX = 0f
    private var offsetY = 0f
    private var effectAlpha = 1f

    private var isAnimating = false
    private var lastFrameTime = 0L

    private val clipPath = Path()
    private val rectF = RectF()
    var cornerRadius: Float = 16f * resources.displayMetrics.density

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        try {
            val customTypeface = ResourcesCompat.getFont(context, R.font.plus_jakarta_sans_bold)
            textPaint.typeface = customTypeface ?: Typeface.DEFAULT_BOLD
            glowPaint.typeface = customTypeface ?: Typeface.DEFAULT_BOLD
            neonPaint.typeface = customTypeface ?: Typeface.DEFAULT_BOLD
        } catch (_: Exception) {
            textPaint.typeface = Typeface.DEFAULT_BOLD
            glowPaint.typeface = Typeface.DEFAULT_BOLD
            neonPaint.typeface = Typeface.DEFAULT_BOLD
        }
    }

    fun setLedText(text: String) {
        this.text = text
        invalidate()
    }

    fun setLedFontSize(sizePx: Int) {
        this.fontSizePx = sizePx.toFloat()
        invalidate()
    }

    fun setLedScrollSpeed(speedSec: Int) {
        this.scrollSpeedSec = speedSec.coerceIn(1, 10)
    }

    fun setLedTextColor(color: Int) {
        this.textColor = color
        invalidate()
    }

    fun setLedDirection(direction: LedDirection) {
        this.direction = direction
        resetPosition()
        invalidate()
    }

    fun setLedVisualEffect(effect: LedVisualEffect) {
        this.visualEffect = effect
        invalidate()
    }

    fun setLedBackgroundRes(resId: Int?) {
        this.backgroundResId = resId
        this.backgroundBitmap = null
        invalidate()
    }

    fun setLedBackgroundUri(uriString: String?) {
        this.backgroundResId = null
        this.backgroundBitmap = null
        if (!uriString.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(uriString)
                val inputStream = context.contentResolver.openInputStream(uri)
                this.backgroundBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } catch (_: Exception) {}
        }
        invalidate()
    }

    fun setLedBackgroundAsset(assetPath: String?) {
        this.backgroundResId = null
        this.backgroundBitmap = null
        if (!assetPath.isNullOrEmpty()) {
            try {
                val inputStream = context.assets.open(assetPath)
                this.backgroundBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            } catch (_: Exception) {}
        }
        invalidate()
    }

    private fun resetPosition() {
        when (direction) {
            LedDirection.LEFT -> offsetX = width.toFloat()
            LedDirection.RIGHT -> offsetX = -textBounds.width().toFloat()
            LedDirection.UP -> offsetY = height.toFloat()
            LedDirection.DOWN -> offsetY = -textBounds.height().toFloat()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectF.set(0f, 0f, w.toFloat(), h.toFloat())
        clipPath.reset()
        if (cornerRadius > 0f) {
            clipPath.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
        }
        resetPosition()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    fun startAnimation() {
        if (isAnimating) return
        isAnimating = true
        lastFrameTime = System.currentTimeMillis()
        postInvalidateOnAnimation()
    }

    fun stopAnimation() {
        isAnimating = false
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (cornerRadius > 0f) {
            canvas.clipPath(clipPath)
        }

        // Draw Background
        val bmp = backgroundBitmap
        if (bmp != null) {
            val src = Rect(0, 0, bmp.width, bmp.height)
            val dst = Rect(0, 0, width, height)
            canvas.drawBitmap(bmp, src, dst, bgPaint)
        } else if (backgroundResId != null && backgroundResId != 0) {
            val drawable = ContextCompat.getDrawable(context, backgroundResId!!)
            drawable?.setBounds(0, 0, width, height)
            drawable?.draw(canvas)
        } else {
            canvas.drawColor(Color.parseColor("#121316"))
        }

        if (text.isEmpty()) return

        textPaint.textSize = fontSizePx
        glowPaint.textSize = fontSizePx
        neonPaint.textSize = fontSizePx

        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val textWidth = textPaint.measureText(text)
        val textHeight = textBounds.height().toFloat()

        // Calculate motion step
        val currentTime = System.currentTimeMillis()
        val dt = if (lastFrameTime == 0L) 0.016f else (currentTime - lastFrameTime) / 1000f
        lastFrameTime = currentTime

        // Speed in px/s based on speed seconds
        val speedPxPerSec = when (direction) {
            LedDirection.LEFT, LedDirection.RIGHT -> (width + textWidth) / scrollSpeedSec.toFloat()
            LedDirection.UP, LedDirection.DOWN -> (height + textHeight) / scrollSpeedSec.toFloat()
        }

        when (direction) {
            LedDirection.LEFT -> {
                offsetX -= speedPxPerSec * dt
                if (offsetX < -textWidth) {
                    offsetX = width.toFloat()
                }
                offsetY = (height + textHeight) / 2f - textBounds.bottom
            }
            LedDirection.RIGHT -> {
                offsetX += speedPxPerSec * dt
                if (offsetX > width) {
                    offsetX = -textWidth
                }
                offsetY = (height + textHeight) / 2f - textBounds.bottom
            }
            LedDirection.UP -> {
                offsetY -= speedPxPerSec * dt
                if (offsetY < -textHeight) {
                    offsetY = height.toFloat() + textHeight
                }
                offsetX = (width - textWidth) / 2f
            }
            LedDirection.DOWN -> {
                offsetY += speedPxPerSec * dt
                if (offsetY > height + textHeight) {
                    offsetY = -textHeight
                }
                offsetX = (width - textWidth) / 2f
            }
        }

        // Apply visual effect calculations
        when (visualEffect) {
            LedVisualEffect.BLINK -> {
                val cycle = (currentTime % 800) / 800f
                effectAlpha = if (cycle < 0.5f) 1f else 0.1f
            }
            LedVisualEffect.FADE -> {
                val cycle = (currentTime % 2000) / 2000f
                effectAlpha = (Math.sin(cycle * Math.PI * 2).toFloat() + 1f) / 2f
                effectAlpha = effectAlpha.coerceIn(0.15f, 1f)
            }
            else -> {
                effectAlpha = 1f
            }
        }

        val alphaInt = (Color.alpha(textColor) * effectAlpha).toInt().coerceIn(0, 255)
        val appliedColor = Color.argb(alphaInt, Color.red(textColor), Color.green(textColor), Color.blue(textColor))

        when (visualEffect) {
            LedVisualEffect.GLOW -> {
                glowPaint.color = appliedColor
                glowPaint.setShadowLayer(25f * resources.displayMetrics.density, 0f, 0f, appliedColor)
                canvas.drawText(text, offsetX, offsetY, glowPaint)

                textPaint.color = appliedColor
                canvas.drawText(text, offsetX, offsetY, textPaint)
            }
            LedVisualEffect.NEON -> {
                // Outer wide glow
                neonPaint.style = Paint.Style.STROKE
                neonPaint.strokeWidth = 6f * resources.displayMetrics.density
                neonPaint.color = appliedColor
                neonPaint.setShadowLayer(20f * resources.displayMetrics.density, 0f, 0f, appliedColor)
                canvas.drawText(text, offsetX, offsetY, neonPaint)

                // Inner bright text
                textPaint.style = Paint.Style.FILL
                textPaint.color = Color.WHITE
                textPaint.alpha = alphaInt
                canvas.drawText(text, offsetX, offsetY, textPaint)
            }
            else -> {
                textPaint.style = Paint.Style.FILL
                textPaint.color = appliedColor
                textPaint.clearShadowLayer()
                canvas.drawText(text, offsetX, offsetY, textPaint)
            }
        }

        if (isAnimating) {
            postInvalidateOnAnimation()
        }
    }
}
