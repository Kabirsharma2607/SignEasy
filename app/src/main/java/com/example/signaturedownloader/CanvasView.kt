package com.example.signaturedownloader


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()
    private var mCanvas: Canvas? = null
    private var mBitmap: Bitmap? = null
    private var mCanvasColor: Int = Color.WHITE
    private var mStrokeColor: Int = Color.BLACK
    private var mStrokeWidth: Float = 8f

    init {
        mPaint.apply {
            color = mStrokeColor
            style = Paint.Style.STROKE
            strokeWidth = mStrokeWidth
            isAntiAlias = true
            isDither = true
        }
    }

    fun setCanvasColor(color: Int) {
        mCanvasColor = color
    }

    fun setStrokeColor(color: Int) {
        mStrokeColor = color
        mPaint.color = mStrokeColor
    }

    fun setStrokeWidth(width: Float) {
        mStrokeWidth = width
        mPaint.strokeWidth = mStrokeWidth
    }

    fun clearCanvas() {
        mPath.reset()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
        mCanvas?.drawColor(mCanvasColor)
    }

    override fun onDraw(canvas: Canvas) {
        if (canvas != null) {
            super.onDraw(canvas)
        }
        canvas?.drawBitmap(mBitmap!!, 0f, 0f, null)
        canvas?.drawPath(mPath, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath.moveTo(x!!, y!!)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mPath.lineTo(x!!, y!!)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                mCanvas?.drawPath(mPath, mPaint)
                mPath.reset()
                invalidate()
             }
        }
        return super.onTouchEvent(event)
    }
}
