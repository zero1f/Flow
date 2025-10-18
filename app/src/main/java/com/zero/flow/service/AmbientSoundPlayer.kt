package com.zero.flow.service

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
        AmbientSoundType.SLOWTICKING -> R.raw.ticking_slow
        AmbientSoundType.FASTTICKING -> R.raw.ticking_fast
        else -> null
    }
}
