package com.zero.flow.service

import android.content.Context
import androidx.annotation.RawRes
import com.zero.flow.R
import com.zero.flow.domain.model.AmbientSoundType

fun AmbientSoundType.toRawResId(): Int? {
    return when (this) {
        AmbientSoundType.FIRE -> R.raw.fire
        AmbientSoundType.RAIN -> R.raw.rain
        AmbientSoundType.WIND -> R.raw.wind
        AmbientSoundType.OCEAN -> R.raw.ocean
        AmbientSoundType.FOREST -> R.raw.forest
        AmbientSoundType.THUNDER -> R.raw.thunder
        else -> null
    }
}
