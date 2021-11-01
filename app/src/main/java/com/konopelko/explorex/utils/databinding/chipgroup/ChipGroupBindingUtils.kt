package com.konopelko.explorex.utils.databinding.chipgroup

import androidx.core.view.ViewCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

@BindingAdapter(value = ["categoriesChips"], requireAll = false)
fun setChipGroupCityChips(
    viewGroup: ChipGroup,
    categories: List<String>?
) {
    categories?.forEach { category ->
        val chip = Chip(viewGroup.context)
//        chip.setChipBackgroundColorResource(R.color.white)
//        chip.chipStrokeColor = chip.getLoginChipColorStateList()
//        chip.setChipStrokeWidthResource(R.dimen.spacing_xs)
//        chip.setTextColor(chip.getLoginChipColorStateList())
        chip.isCheckable = false
        chip.text = category
        chip.id = ViewCompat.generateViewId()
        viewGroup.addView(chip)
    }
}