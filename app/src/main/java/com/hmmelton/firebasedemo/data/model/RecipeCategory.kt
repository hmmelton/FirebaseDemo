package com.hmmelton.firebasedemo.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class RecipeCategory(
    @StringRes val nameResource: Int,
    @DrawableRes val imageResource: Int
)
