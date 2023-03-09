/*
 * Created by Radu Stavila on 3/6/23, 6:12 PM
 *     stavila.radu@yahoo.com
 *     Last modified 3/8/23, 7:32 PM
 *     Copyright (c) 2023.
 *     All rights reserved.
 */

package com.radustavila.quantitycardviewexample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources
import com.radustavila.qunatitycardlibrary.QuantityCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val quantityCard: QuantityCardView = findViewById(R.id.third_card)
        quantityCard.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, resources.displayMetrics).toInt()
        quantityCard.height = 250
        quantityCard.setMinQuantity(1000)
        quantityCard.setMaxQuantity(7777)
        quantityCard.setStartingQuantity(99999)
        quantityCard.setTextColor(Color.WHITE)
        quantityCard.setLayoutBackgroundColor(Color.BLACK)
        quantityCard.setTextSize(30f)
        quantityCard.setIncreaseImage(AppCompatResources.getDrawable(this, R.drawable.plus))
        quantityCard.setDecreaseImage(AppCompatResources.getDrawable(this, R.drawable.minus))
        quantityCard.setTransitionDuration(500)
        quantityCard.setReverseAnimation(true)
    }
}