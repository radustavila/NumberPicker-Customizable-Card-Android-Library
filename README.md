# Plus-Minus Quantity Card Library Android 


A simple and fun UI widget cardview for displaying a plus-minus animated quantity layout.
You can customize the plus & minus views! Setting image drawables for plus & minus available.

![QuantityCardViewExample](https://user-images.githubusercontent.com/62139439/224288780-f2f66dd3-edc8-4ad7-ab54-1d07b49c88b1.gif)

## Including into your project

### Gradle 

Add below codes to your <b>root</b> settings.gradle file.

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

And add a dependency code to your <b>module</b>'s build.gradle file.

```
dependencies {
      implementation 'com.github.radustavila:Plus-Minus-Quantity-Card-Android-Library:1.1'
}
```

## Usage

- Default in your xml file:

```
  <com.radustavila.qunatitycardlibrary.QuantityCardView
      android:id="@+id/quantity_card"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      />
```

- Custom in your xml file:

```
  <com.radustavila.qunatitycardlibrary.QuantityCardView
      android:id="@+id/second_card"
      android:layout_width="200dp"
      android:layout_height="65dp"
      android:layout_margin="20dp"
      android:textSize="7sp"
      android:textColor="@color/teal_700"
      app:decreaseTextColor="@color/purple_200"
      app:increaseTextColor="@color/purple_700"
      app:decreaseTextSize="13sp"
      app:increaseTextSize="13sp"
      app:startingQuantity="1"
      app:minQuantity="0"
      app:maxQuantity="10"
      app:reverseAnimation="false"
      app:transitionDuration="300"
      app:layoutBackgroundColor="@color/gray"
      />
```

- Custom in your Activity/Fragment/View file:

```
    val quantityCard: QuantityCardView = findViewById(R.id.third_card)
    quantityCard.width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, resources.displayMetrics).toInt()
    quantityCard.height = 250
    quantityCard.setMinQuantity(1000)
    quantityCard.setMaxQuantity(7777)
    quantityCard.setStartingQuantity(99999)
    quantityCard.setTextColor(Color.WHITE)
    quantityCard.setLayoutBackgroundColor(Color.BLACK)
    quantityCard.setTextSize(30f)
    quantityCard.setTransitionDuration(500)
    quantityCard.setReverseAnimation(true)
    quantityCard.setIncreaseImage(AppCompatResources.getDrawable(this, R.drawable.plus))  // Add your own drawable!
    quantityCard.setDecreaseImage(AppCompatResources.getDrawable(this, R.drawable.minus)) // Add your own drawable!
```

### QuantityCardView attributes

| Attributes        | Type           | Default  | Description 
| ------------------|:--------------:| --------:|------------: 
| android:width | Dimension Pixel | 160dp | Card's width 
| android:height | Dimension Pixel | 55dp | Card's height
| android:textSize | Dimension |   14sp    | Quantity's value size
| android:textColor | ColorInt | Color.Black | Quantity's value color
| android:fontFamily | Typeface | Android default | Quantity's value typeface
| app:startingQuantity | Integer | 0 | Quantity starting value
| app:minQuantity | Integer | -1999999999 | Quantity minimum value
| app:maxQuantity | Integer | 1999999999 | Quantity maximum value
| app:transitionDuration | Integer | 250 | Duration for animation transition
| app:reverseAnimation | Boolean | false | Animation transition direction
| app:layoutBackgroundColor | ColorInt | Color.WHITE | Card's background color
| app:buttonsTextColor | ColorInt | Color.DKGRAY | Increase & Decrease TextView's color
| app:increaseTextColor | ColorInt | Color.DKGRAY | Increase TextView's color
| app:decreaseTextColor | ColorInt | Color.DKGRAY | Decrease TextView's color
| app:disabledButtonsTextColor | ColorInt | Color.LTGRAY | Increase & Decrease TextView's color when disabled
| app:buttonsTextSize | Dimension | 13sp | Increase & Decrease TextView's size
| app:increaseTextSize | Dimension | 13sp | Increase TextView's size
| app:decreaseTextSize | Dimension | 13sp | Decrease TextView's size
| app:increaseImageSrc | Reference | null | Image drawable for increase view
| app:decreaseImageSrc | Reference | null | Image drawable for decrease view
| app:increaseImageDisabledSrc | Reference | null | Image drawable for increase view when disabled
| app:decreaseImageDisabledSrc | Reference | null | Image drawable for decrease view when disabled



