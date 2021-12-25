package com.primex.extra

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.material.ContentAlpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


private val PaddingSmall = 4.dp
private val PaddingMedium = 8.dp
private val PaddingLarge = 16.dp
private val PaddingExtraLarge = 32.dp

/** A Padding of 4.dp */
val Dp.Companion.pSmall get() = com.primex.extra.PaddingSmall

/** A Padding of 8.dp */
val Dp.Companion.pNormal get() = PaddingMedium

/** A Padding of 16.dp */
val Dp.Companion.pLarge get() = com.primex.extra.PaddingLarge

/** A Padding of 32.dp */
val Dp.Companion.pLarge2 get() = PaddingExtraLarge


const val AnimDurationShort = 250
const val AnimDurationMedium = 500
const val AnimDurationLong = 750

typealias Anim = AnimationConstants

/**A Duration of 250 mills*/
val Anim.durationShort get() = AnimDurationShort

/**A Duration of 500 mills*/
val Anim.durationMedium get() = AnimDurationMedium

/**A Duration of 750 mills*/
val Anim.durationLong get() = AnimDurationLong

private val elevationNone = 0.dp
private val elevationLow = 6.dp
private val elevationMedium = 12.dp
private val elevationHigh = 20.dp
private val elevationExtraHigh = 30.dp

/**An Elevation of 0 dp*/
val Dp.Companion.eNone: Dp get() = com.primex.extra.elevationNone

/**An Elevation of 6 dp*/
val Dp.Companion.eLow: Dp get() = com.primex.extra.elevationLow

/**An Elevation of 12 dp*/
val Dp.Companion.eMedium: Dp get() = com.primex.extra.elevationMedium

/**An Elevation of 20 dp*/
val Dp.Companion.eHigh: Dp get() = com.primex.extra.elevationHigh

/**An Elevation of 30 dp*/
val Dp.Companion.eExtraHigh: Dp get() = com.primex.extra.elevationExtraHigh

private const val Divider = 0.12f

/**
 * The recommended divider Alpha
 */
val ContentAlpha.Divider get() = com.primex.extra.Divider

private const val Indication = 0.1f

/**
 * The recommended LocalIndication Alpha
 */
val ContentAlpha.Indication get() = com.primex.extra.Indication