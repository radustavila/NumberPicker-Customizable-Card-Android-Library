/*
 * Created by Radu Stavila on 3/9/23, 6:13 PM
 *     stavila.radu@yahoo.com
 *     Last modified 3/8/23, 7:40 PM
 *     Copyright (c) 2023.
 *     All rights reserved.
 */

package com.radustavila.qunatitycardlibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding

/*
 * Helper constants
 */
private const val RES_ANDROID = "http://schemas.android.com/apk/res/android"
private const val LAYOUT_HEIGHT = "layout_height"
private const val LAYOUT_WIDTH = "layout_width"
private const val FONT_AWESOME_PRO_PATH = "fonts/fontawesomepro.otf"

/*
 * Default constants
 */
private const val EMPTY = 0
private const val DEFAULT_STARTING_QUANTITY = 0
private const val DEFAULT_MIN_QUANTITY = -1999999999
private const val DEFAULT_MAX_QUANTITY = 1999999999
private const val DEFAULT_HEIGHT = 55
private const val DEFAULT_WIDTH = 160
private const val DEFAULT_TEXT_SIZE_QUANTITY = 25f
private const val DEFAULT_TEXT_SIZE_INCREASE = 20f
private const val DEFAULT_TEXT_SIZE_DECREASE = 20f
private const val DEFAULT_TEXT_COLOR_QUANTITY = Color.BLACK
private const val DEFAULT_TEXT_COLOR_BUTTONS = Color.DKGRAY
private const val DEFAULT_TEXT_COLOR_DISABLED = Color.LTGRAY
private const val DEFAULT_LAYOUT_BACKGROUND_COLOR = Color.WHITE
private const val DEFAULT_TRANSITION_DURATION = 250L
private const val DEFAULT_REVERSE_TRANSITION = false
private const val DEFAULT_RELOAD_VIEWS = false

/**
 * Compound View made up of Decrease (ImageView), Quantity (TextView) & Increase (ImageView).
 * Default Quantity = 0, Background = White, Text = Black.
 * Constraints: maxHeight = 100dp, layout_gravity = center.
 */
class QuantityCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val mContext = context

    /*
     * Views
     */
    private var mDecreaseText: TextView
    private var mIncreaseText: TextView
    private var mDecreaseImage: ImageView
    private var mIncreaseImage: ImageView
    private var mQuantityView: TextSwitcher
    private var mQuantityLayout: LinearLayout

    /*
     * Properties
     */
    private var mTransitionDuration = DEFAULT_TRANSITION_DURATION
    private var mReverseTransition = DEFAULT_REVERSE_TRANSITION
    private var mReloadViews = DEFAULT_RELOAD_VIEWS
    private var mIncreaseTextColor = DEFAULT_TEXT_COLOR_BUTTONS
    private var mDecreaseTextColor = DEFAULT_TEXT_COLOR_BUTTONS
    private var mIncreaseTextSize = DEFAULT_TEXT_SIZE_INCREASE
    private var mDecreaseTextSize = DEFAULT_TEXT_SIZE_DECREASE
    private var mLayoutBackgroundColor = DEFAULT_LAYOUT_BACKGROUND_COLOR
    private var mTextColor = DEFAULT_TEXT_COLOR_QUANTITY
    private var mTextColorDisabled = DEFAULT_TEXT_COLOR_DISABLED
    private var mTextSize = DEFAULT_TEXT_SIZE_QUANTITY
    private var mHeight = EMPTY
    private var mWidth = EMPTY
    private var mQuantity = DEFAULT_STARTING_QUANTITY
    private var mMaxQuantity = DEFAULT_MAX_QUANTITY
    private var mMinQuantity = DEFAULT_MIN_QUANTITY

    private var mTypeface: Typeface? = null
    private var mIncreaseImageDrawable: Drawable? = null
    private var mDecreaseImageDrawable: Drawable? = null
    private var mIncreaseImageDisabledDrawable: Drawable? = null
    private var mDecreaseImageDisabledDrawable: Drawable? = null

    /*
     * Transition animations for quantity view
     */
    private val animSlideUpIn = AnimationUtils.loadAnimation(context, R.anim.slide_up_in)
    private val animSlideUpOut = AnimationUtils.loadAnimation(context, R.anim.slide_up_out)
    private val animSlideDownIn = AnimationUtils.loadAnimation(context, R.anim.slide_down_in)
    private val animSlideDownOut = AnimationUtils.loadAnimation(context, R.anim.slide_down_out)


    /**
     * Initialize QuantityCardView's views and attributes
     */
    init {
        LayoutInflater.from(context).inflate(R.layout.quantity_cardview, this, true)

        // ViewGroup sub-classes do not call their onDraw method
        setWillNotDraw(false)

        // Initialize views
        mDecreaseText = findViewById(R.id.decrease_text)
        mDecreaseImage = findViewById(R.id.decrease_image)
        mIncreaseText = findViewById(R.id.increase_text)
        mIncreaseImage = findViewById(R.id.increase_image)
        mQuantityView = findViewById(R.id.quantity)
        mQuantityLayout = findViewById(R.id.quantity_layout)

        // Get layout attributes
        context.theme.obtainStyledAttributes(attrs, R.styleable.QuantityCardView, EMPTY, EMPTY).apply {
            try {
                mReverseTransition = getBoolean(R.styleable.QuantityCardView_reverseAnimation, DEFAULT_REVERSE_TRANSITION)
                mTransitionDuration = getInt(R.styleable.QuantityCardView_transitionDuration, DEFAULT_TRANSITION_DURATION.toInt()).toLong()
                mLayoutBackgroundColor = getColor(R.styleable.QuantityCardView_layoutBackgroundColor, DEFAULT_LAYOUT_BACKGROUND_COLOR)
                mTextColor = getColor(R.styleable.QuantityCardView_android_textColor, DEFAULT_TEXT_COLOR_QUANTITY)
                mTextSize = getDimension(R.styleable.QuantityCardView_android_textSize, DEFAULT_TEXT_SIZE_QUANTITY)

                mDecreaseTextColor = getColor(R.styleable.QuantityCardView_decreaseTextColor, DEFAULT_TEXT_COLOR_BUTTONS)
                mIncreaseTextColor = getColor(R.styleable.QuantityCardView_increaseTextColor, DEFAULT_TEXT_COLOR_BUTTONS)
                mIncreaseTextSize = getDimension(R.styleable.QuantityCardView_increaseTextSize, DEFAULT_TEXT_SIZE_INCREASE)
                mDecreaseTextSize = getDimension(R.styleable.QuantityCardView_decreaseTextSize, DEFAULT_TEXT_SIZE_DECREASE)
                if (getColor(R.styleable.QuantityCardView_buttonsTextColor, EMPTY) != EMPTY) {
                    mDecreaseTextColor = getColor(R.styleable.QuantityCardView_buttonsTextColor, DEFAULT_TEXT_COLOR_BUTTONS)
                    mIncreaseTextColor = getColor(R.styleable.QuantityCardView_buttonsTextColor, DEFAULT_TEXT_COLOR_BUTTONS)
                }
                if (getDimension(R.styleable.QuantityCardView_buttonsTextSize, EMPTY.toFloat()) != EMPTY.toFloat()) {
                    mIncreaseTextSize = getDimension(R.styleable.QuantityCardView_buttonsTextSize, DEFAULT_TEXT_SIZE_INCREASE)
                    mDecreaseTextSize = getDimension(R.styleable.QuantityCardView_buttonsTextSize, DEFAULT_TEXT_SIZE_DECREASE)
                }
                mIncreaseImageDrawable = getDrawable(R.styleable.QuantityCardView_increaseImageSrc)
                mDecreaseImageDrawable = getDrawable(R.styleable.QuantityCardView_decreaseImageSrc)
                mIncreaseImageDisabledDrawable = getDrawable(R.styleable.QuantityCardView_increaseImageDisabledSrc)
                mDecreaseImageDisabledDrawable = getDrawable(R.styleable.QuantityCardView_decreaseImageDisabledSrc)

                mQuantity = getInt(R.styleable.QuantityCardView_startingQuantity, DEFAULT_STARTING_QUANTITY)
                mMaxQuantity = getInt(R.styleable.QuantityCardView_maxQuantity, DEFAULT_MAX_QUANTITY)
                mMinQuantity = getInt(R.styleable.QuantityCardView_minQuantity, DEFAULT_MIN_QUANTITY)

                if (mMinQuantity > mQuantity) mMinQuantity = mQuantity
                if (mMaxQuantity < mQuantity) mMaxQuantity = mQuantity

                val layoutHeight = attrs?.getAttributeValue(RES_ANDROID, LAYOUT_HEIGHT)
                val layoutWidth = attrs?.getAttributeValue(RES_ANDROID, LAYOUT_WIDTH)

                when {
                    layoutHeight.equals(ViewGroup.LayoutParams.MATCH_PARENT.toString()) ->
                        mHeight = ViewGroup.LayoutParams.MATCH_PARENT
                    layoutHeight.equals(ViewGroup.LayoutParams.WRAP_CONTENT.toString()) ->
                        mHeight = LayoutParams.WRAP_CONTENT
                    else -> context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.layout_height)).apply {
                        mHeight = getDimensionPixelSize(0, DEFAULT_HEIGHT)
                        recycle()
                    }
                }

                when {
                    layoutWidth.equals(ViewGroup.LayoutParams.MATCH_PARENT.toString()) ->
                        mWidth = ViewGroup.LayoutParams.MATCH_PARENT
                    layoutWidth.equals(ViewGroup.LayoutParams.WRAP_CONTENT.toString()) ->
                        mWidth = LayoutParams.WRAP_CONTENT
                    else -> context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.layout_width)).apply {
                        mWidth = getDimensionPixelSize(0, DEFAULT_WIDTH)
                        recycle()
                    }
                }

                val fontFamilyId = getResourceId(R.styleable.QuantityCardView_android_fontFamily, EMPTY)
                if (fontFamilyId > EMPTY) {
                    mTypeface = ResourcesCompat.getFont(getContext(), fontFamilyId)
                }
            } finally {
                recycle()
            }
        }
    }

    /**
     * Click listener for increasing quantity value, add animations for value transitions
     */
    private val increaseClickListener = OnClickListener {
        if (mQuantity < mMaxQuantity) {
            if (mReverseTransition) {
                mQuantityView.inAnimation = animSlideUpIn
                mQuantityView.outAnimation = animSlideUpOut
            } else {
                mQuantityView.inAnimation = animSlideDownIn
                mQuantityView.outAnimation = animSlideDownOut
            }

            updateQuantity(true)

            if (mQuantity == mMaxQuantity)
                setViewsProperties(mIncreaseImageDrawable, mIncreaseImage, mIncreaseImageDisabledDrawable, mIncreaseText, mTextColorDisabled)
            if (mQuantity - 1 == mMinQuantity)
                setViewsProperties(mDecreaseImageDrawable, mDecreaseImage, mDecreaseImageDrawable, mDecreaseText, mDecreaseTextColor)
        }
    }

    /**
     * Click listener for decreasing quantity value, add animations for value transitions
     */
    private val decreaseClickListener = OnClickListener {
        if (mQuantity > mMinQuantity) {
            if (mReverseTransition) {
                mQuantityView.inAnimation = animSlideDownIn
                mQuantityView.outAnimation = animSlideDownOut
            } else {
                mQuantityView.inAnimation = animSlideUpIn
                mQuantityView.outAnimation = animSlideUpOut
            }

            updateQuantity(false)

            if (mQuantity == mMinQuantity)
                setViewsProperties(mDecreaseImageDrawable, mDecreaseImage, mDecreaseImageDisabledDrawable, mDecreaseText, mTextColorDisabled)
            if (mQuantity + 1 == mMaxQuantity)
                setViewsProperties(mIncreaseImageDrawable, mIncreaseImage, mIncreaseImageDrawable, mIncreaseText, mIncreaseTextColor)
        }
    }

    /**
     * Set properties for TextView Or ImageView. Check whether image is assigned
     * instead of text and act accordingly.
     * Used for switching from disabled to enabled and vice versa.
     */
    private fun setViewsProperties(
        imageAssigned: Drawable?,
        imageView: ImageView,
        imageDrawable: Drawable?,
        textView: TextView,
        @ColorInt textColor: Int
    ) {
        if (imageAssigned != null)
            imageDrawable?.let {
                imageView.setImageDrawable(it)
            }
        else
            textView.setTextColor(textColor)
    }

    /**
     * Update quantity value and set mQuantityView Switcher's new text.
     */
    private fun updateQuantity(increaseValue: Boolean) {
        mQuantity = if (increaseValue) mQuantity + 1 else mQuantity - 1
        mQuantityView.setText(" $mQuantity ")
    }

    /**
     * Get QuantityCardView's quantity value.
     */
    fun getQuantity() = mQuantity

    /**
     * Set the starting value of the QuantityCardView's quantity (TextSwitcher).
     */
    fun setStartingQuantity(quantity: Int) {
        mQuantity = quantity
        if (mQuantity > mMaxQuantity) mMaxQuantity = mQuantity
        if (mQuantity < mMinQuantity) mMinQuantity = mQuantity
        mQuantityView.setCurrentText(" $mQuantity ")
    }

    /**
     * Set the minimum value of the QuantityCardView's quantity (TextSwitcher).
     */
    fun setMinQuantity(quantity: Int) {
        mMinQuantity = quantity
    }

    /**
     * Set the maximum value of the QuantityCardView's quantity (TextSwitcher).
     */
    fun setMaxQuantity(quantity: Int) {
        mMaxQuantity = quantity
    }

    /**
     * Set Increasing' TextView text size.
     */
    fun setIncreaseTextSize(size: Float) {
        mIncreaseTextSize = size
        mIncreaseText.textSize = mIncreaseTextSize
    }

    /**
     * Set Decreasing' TextView text size.
     */
    fun setDecreaseTextSize(size: Float) {
        mDecreaseTextSize = size
        mDecreaseText.textSize = mDecreaseTextSize
    }

    /**
     * Set Decreasing' & Increasing' TextViews text size.
     */
    fun setButtonsTextSize(size: Float) {
        mIncreaseTextSize = size
        mDecreaseTextSize = size
        mIncreaseText.textSize = mIncreaseTextSize
        mDecreaseText.textSize = mDecreaseTextSize
    }

    /**
     * Set Increase' & Decrease' Images drawable when disabled.
     */
    private fun setImageDrawable(imageView: ImageView, imageDisabledDrawable: Drawable?) {
        imageDisabledDrawable?.let {
            imageView.setImageDrawable(it)
        }
    }

    /**
     * Set Increasing' TextView text color.
     */
    fun setIncreaseTextColor(@ColorInt color: Int) {
        mIncreaseTextColor = color
        mIncreaseText.setTextColor(mIncreaseTextColor)
    }

    /**
     * Set Decreasing' TextView text color.
     */
    fun setDecreaseTextColor(@ColorInt color: Int) {
        mDecreaseTextColor = color
        mDecreaseText.setTextColor(mDecreaseTextColor)
    }

    /**
     * Set Increasing' and Decreasing' TextViews text uniform color.
     */
    fun setButtonsTextColor(@ColorInt color: Int) {
        mIncreaseTextColor = color
        mDecreaseTextColor = color
        setIncreaseTextColor(mIncreaseTextColor)
        setDecreaseTextColor(mDecreaseTextColor)
    }

    /**
     * Set ImageScaleType for the Decreasing ImageView.
     */
    fun setDecreaseImageScaleType(scaleType: ImageView.ScaleType) {
        mDecreaseImage.scaleType = scaleType
    }

    /**
     * Set ImageScaleType for the Increasing ImageView.
     */
    fun setIncreaseImageScaleType(scaleType: ImageView.ScaleType) {
        mIncreaseImage.scaleType = scaleType
    }

    /**
     * Set Increasing' and Decreasing' ImageViews uniform ImageScaleType.
     */
    fun setImagesScaleType(scaleType: ImageView.ScaleType) {
        setDecreaseImageScaleType(scaleType)
        setIncreaseImageScaleType(scaleType)
    }

    /**
     * Set quantity's TextView text color.
     */
    fun setTextColor(@ColorInt color: Int) {
        mTextColor = color

        val textView1 = mQuantityView.getChildAt(0) as TextView
        val textView2 = mQuantityView.getChildAt(1) as TextView

        textView1.setTextColor(mTextColor)
        textView2.setTextColor(mTextColor)
    }

    /**
     * Set quantity's TextView text size.
     */
    fun setTextSize(size: Float) {
        mTextSize = size

        val textView1 = mQuantityView.getChildAt(0) as TextView
        val textView2 = mQuantityView.getChildAt(1) as TextView

        textView1.textSize = mTextSize
        textView2.textSize = mTextSize
    }

    /**
     * Set quantity's TextView typeface - font family.
     */
    fun setTextTypeface(typeface: Typeface?) {
        mTypeface = typeface

        val textView1 = mQuantityView.getChildAt(0) as TextView
        val textView2 = mQuantityView.getChildAt(1) as TextView

        mTypeface?.let {
            textView1.typeface = it
            textView2.typeface = it
        }
    }

    /**
     * Switch visibility between 2 views.
     * If visibility is true then view1 is gone and view2 is visible.
     * Else view1 is visible and view2 is gone.
     */
    private fun switchVisibility(view1Visible: View, view2Gone: View) {
        view1Visible.visibility = View.VISIBLE
        view2Gone.visibility = View.GONE
    }

    /**
     * Set image for the decreasing view.
     * By default it is hidden and a TextView is visible with +.
     * If a drawable is set, then the ImageView is visible and TextView is gone.
     * Else if the drawable is = null then the TextView is visible and ImageView is gone.
     */
    fun setDecreaseImage(drawable: Drawable?) {
        if (drawable == null) {
            switchVisibility(mDecreaseText, mDecreaseImage)
        } else {
            mDecreaseImageDrawable = drawable
            mDecreaseImage.setImageDrawable(drawable)
            switchVisibility(mDecreaseImage, mDecreaseText)
        }
    }

    /**
     * Set image for the increasing view.
     * By default it is hidden and a TextView is visible with +.
     * If a drawable is set, then the ImageView is visible and TextView is gone.
     * Else if the drawable is = null then the TextView is visible and ImageView is gone.
     */
    fun setIncreaseImage(drawable: Drawable?) {
        if (drawable == null) {
            switchVisibility(mIncreaseText, mIncreaseImage)
        } else {
            mIncreaseImageDrawable = drawable
            mIncreaseImage.setImageDrawable(drawable)
            switchVisibility(mIncreaseImage, mIncreaseText)
        }
    }

    /**
     * Set image for the decreasing view when disabled.
     * It will be displayed only if an image was assigned
     * using setDecreaseImage method.
     */
    fun setDecreaseDisabledImage(drawable: Drawable?) {
        drawable?.let {
            mDecreaseImageDisabledDrawable = it
        }
    }

    /**
     * Set image for the increasing view when disabled.
     * It will be displayed only if an image was assigned
     * using setIncreaseImage method.
     */
    fun setIncreaseDisabledImage(drawable: Drawable?) {
        drawable?.let {
            mIncreaseImageDisabledDrawable = it
        }
    }

    /**
     * Set background color for QuantityCardView layout.
     */
    fun setLayoutBackgroundColor(@ColorInt color: Int) {
        mLayoutBackgroundColor = color
        mQuantityLayout.setBackgroundColor(mLayoutBackgroundColor)
    }

    /**
     * Set transition duration for Quantity TextSwitcher's animations.
     */
    fun setTransitionDuration(duration: Long) {
        mTransitionDuration = duration
        setAnimationsTransitionDuration()
    }

    /**
     * Set QuantityCardView's quantity transition direction:
     * False (Default) - when add the views go down and when decrease the views go up.
     * True - when add the views go up and when decrease the views go down.
     */
    fun setReverseAnimation(reverseAnimation: Boolean) {
        mReverseTransition = reverseAnimation
    }

    /**
     * Set the padding for the increasing view.
     */
    fun setIncreaseImagePadding(size: Int) {
        mIncreaseImage.setPadding(size)
        mIncreaseText.setPadding(size)
    }

    /**
     * Set the padding for the increasing view.
     */
    fun setIncreaseImagePadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        mIncreaseImage.setPadding(left, top, right, bottom)
        mIncreaseText.setPadding(left, top, right, bottom)
    }

    /**
     * Set the padding for the decreasing view.
     */
    fun setDecreaseViewPadding(size: Int) {
        mIncreaseImage.setPadding(size)
        mIncreaseText.setPadding(size)
    }

    /**
     * Set the padding for the decreasing view.
     */
    fun setDecreaseViewPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
        mIncreaseImage.setPadding(left, top, right, bottom)
        mIncreaseText.setPadding(left, top, right, bottom)
    }

    /**
     * Set QuantityCardView's width.
     */
    fun setWidth(width: Int) {
        mWidth = width
        refreshLayout()
    }

    /**
     * Set QuantityCardView's height.
     */
    fun setHeight(height: Int) {
        mHeight = height
        refreshLayout()
    }

    /**
     * Redraw layout.
     */
    private fun refreshLayout() {
        invalidate()
        requestLayout()
    }

    /**
     * Set listeners, text switcher factory & assets
     */
    override fun onFinishInflate() {
        super.onFinishInflate()

        setAnimationsTransitionDuration()

        mDecreaseText.setOnClickListener(decreaseClickListener)
        mDecreaseImage.setOnClickListener(decreaseClickListener)
        mIncreaseText.setOnClickListener(increaseClickListener)
        mIncreaseImage.setOnClickListener(increaseClickListener)

        val fap = Typeface.createFromAsset(mContext.assets, FONT_AWESOME_PRO_PATH)
        mIncreaseText.typeface = fap
        mDecreaseText.typeface = fap
        mIncreaseText.text = mContext.getString(R.string.plus)
        mDecreaseText.text = mContext.getString(R.string.minus)
        mIncreaseText.textSize = mIncreaseTextSize
        mDecreaseText.textSize = mDecreaseTextSize
        mIncreaseText.setTextColor(mIncreaseTextColor)
        mDecreaseText.setTextColor(mDecreaseTextColor)
        mQuantityLayout.setBackgroundColor(mLayoutBackgroundColor)

        mIncreaseImageDrawable?.let {
            mIncreaseImage.setImageDrawable(it)
            switchVisibility(mIncreaseImage, mIncreaseText)
        }

        mDecreaseImageDrawable?.let {
            mDecreaseImage.setImageDrawable(it)
            switchVisibility(mDecreaseImage, mDecreaseText)
        }

        if (mQuantity == mMinQuantity)
            setViewsProperties(mDecreaseImageDrawable, mDecreaseImage, mDecreaseImageDisabledDrawable, mDecreaseText, mTextColorDisabled)
        if (mQuantity == mMaxQuantity)
            setViewsProperties(mIncreaseImageDrawable, mIncreaseImage, mIncreaseImageDisabledDrawable, mIncreaseText, mTextColorDisabled)

        mQuantityView.setFactory {
            val textView = TextView(context)
            mQuantityLayout.post {
                textView.height = mQuantityLayout.height
            }

            textView.gravity = Gravity.CENTER
            textView.typeface = mTypeface
            textView.includeFontPadding = false
            textView.textSize = mTextSize
            textView.setTextColor(mTextColor)
            textView.setPadding(5, -15, 5, -15)

            textView
        }

        // Set first quantity value
        mQuantityView.setCurrentText(" $mQuantity ")
    }

    /**
     * Set duration for each animation.
     */
    private fun setAnimationsTransitionDuration() {
        animSlideUpIn.duration = mTransitionDuration
        animSlideUpOut.duration = mTransitionDuration
        animSlideDownIn.duration = mTransitionDuration
        animSlideDownOut.duration = mTransitionDuration
    }

    /**
     * Set QuantityCardView's height & width.
     * Set default value if any of LayoutParams (height or width) is equal to WRAP_CONTENT
     */
    private fun setHeightAndWidth() {
        layoutParams.height =
            if (mHeight != EMPTY && mHeight != LayoutParams.WRAP_CONTENT) mHeight
            else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_HEIGHT.toFloat(), context.resources.displayMetrics).toInt()

        layoutParams.width =
            if (mWidth != EMPTY && mWidth != LayoutParams.WRAP_CONTENT) mWidth
            else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_WIDTH.toFloat(), context.resources.displayMetrics).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setHeightAndWidth()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mReloadViews) {
            reloadViews()
        }
    }

    /**
     * Set view's properties after configuration changed.
     */
    private fun reloadViews() {
        mQuantityView.setCurrentText(" $mQuantity ")
        mQuantityLayout.setBackgroundColor(mLayoutBackgroundColor)

        val textView1 = mQuantityView.getChildAt(0) as TextView
        val textView2 = mQuantityView.getChildAt(1) as TextView
        textView1.setTextColor(mTextColor)
        textView2.setTextColor(mTextColor)
        textView1.textSize = mTextSize
        textView2.textSize = mTextSize
        mTypeface?.let {
            textView1.typeface = it
            textView2.typeface = it
        }

        mIncreaseText.setTextColor(mIncreaseTextColor)
        mDecreaseText.setTextColor(mDecreaseTextColor)

        if (mDecreaseImageDrawable != null) switchVisibility(mDecreaseImage, mDecreaseText) else switchVisibility(mDecreaseText, mDecreaseImage)
        if (mIncreaseImageDrawable != null) switchVisibility(mIncreaseImage, mIncreaseText) else switchVisibility(mIncreaseText, mIncreaseImage)

        if (mQuantity == mMinQuantity)
            setViewsProperties(mDecreaseImageDrawable, mDecreaseImage, mDecreaseImageDisabledDrawable, mDecreaseText, mTextColorDisabled)
        if (mQuantity == mMaxQuantity)
            setViewsProperties(mIncreaseImageDrawable, mIncreaseImage, mIncreaseImageDisabledDrawable, mIncreaseText, mTextColorDisabled)

        setHeightAndWidth()
        setAnimationsTransitionDuration()

        mReloadViews = false
    }

    /**
     * Save QuantityCardView's mutable properties in a SavedState object.
     */
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return SavedState(
            superState,
            mReverseTransition,
            mTransitionDuration,
            mTypeface,
            mLayoutBackgroundColor,
            mTextColor,
            mIncreaseTextColor,
            mDecreaseTextColor,
            mIncreaseTextSize,
            mDecreaseTextSize,
            mTextSize,
            mQuantity,
            mHeight,
            mWidth,
            true
        )
    }

    /**
     * Reassign QuantityCardView's properties using a SavedState object.
     */
    override fun onRestoreInstanceState(state: Parcelable?) {
        val myState = state as SavedState?
        super.onRestoreInstanceState(myState?.superSavedState ?: state)

        mReverseTransition = myState?.reverseAnimation ?: DEFAULT_REVERSE_TRANSITION
        mTransitionDuration = myState?.transitionDuration ?: DEFAULT_TRANSITION_DURATION
        mReloadViews = myState?.reloadViews ?: DEFAULT_RELOAD_VIEWS
        mLayoutBackgroundColor = myState?.layoutBackgroundColor ?: DEFAULT_LAYOUT_BACKGROUND_COLOR
        mTextColor = myState?.textColor ?: DEFAULT_TEXT_COLOR_QUANTITY
        mIncreaseTextColor = myState?.textColorIncrease ?: DEFAULT_TEXT_COLOR_BUTTONS
        mDecreaseTextColor = myState?.textColorDecrease ?: DEFAULT_TEXT_COLOR_BUTTONS
        mIncreaseTextSize = myState?.textSizeIncrease ?: DEFAULT_TEXT_SIZE_INCREASE
        mDecreaseTextSize = myState?.textSizeDecrease ?: DEFAULT_TEXT_SIZE_DECREASE
        mTextSize = myState?.textSize ?: DEFAULT_TEXT_SIZE_QUANTITY
        mQuantity = myState?.quantity ?: DEFAULT_STARTING_QUANTITY
        mHeight = myState?.height ?: DEFAULT_HEIGHT
        mWidth = myState?.width ?: DEFAULT_WIDTH
        mTypeface = myState?.typeface
    }


    /**
     * State class for QuantityCardView's mutable properties.
     * Save a SavedState object through onSaveInstanceState
     * and retrieve the State object on onRestoreInstanceState
     * where QuantityCardView's properties get reassigned.
     */
    class SavedState(
        val superSavedState: Parcelable?,
        val reverseAnimation: Boolean,
        val transitionDuration: Long,
        val typeface: Typeface?,
        val layoutBackgroundColor: Int,
        val textColor: Int,
        val textColorIncrease: Int,
        val textColorDecrease: Int,
        val textSizeIncrease: Float,
        val textSizeDecrease: Float,
        val textSize: Float,
        val quantity: Int,
        val height: Int,
        val width: Int,
        val reloadViews: Boolean
    ) : BaseSavedState(superSavedState), Parcelable
}