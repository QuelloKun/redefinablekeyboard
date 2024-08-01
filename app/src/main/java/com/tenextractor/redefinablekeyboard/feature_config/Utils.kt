package com.tenextractor.redefinablekeyboard.feature_config

import com.tenextractor.redefinablekeyboard.feature_config.domain.CompiledLayout
import com.tenextractor.redefinablekeyboard.feature_config.domain.Key
import com.tenextractor.redefinablekeyboard.feature_config.domain.KeyWidth

fun compileLayer(layerString: String, lowerLeftKey: Key?, lowerRightKey: Key?,
                 bottomLeftKey: Key, bottomRow: List<Key>, decoupleRows: List<Int>, moveLayerKeys: List<String> = emptyList(),
                 isOtherLayer: Boolean = false
): List<List<Key>> {
    /*
    Creates this layout:
    .   .   .   .   .   .   .   .   .   .   .   .
    .   .   .   .   . layerString   .   .   .   .
    .   .   .   .   .   .   .   .   .   .   .   .
    lowerLeftKey.   .   .   .   .   lowerRightKey
    bottomLeftKey <-         bottomRow         ->
     */
    val rowStrings = layerString.split('\n').map { it.trim() }
    //return rowStrings.map { rowString -> rowString.split(' ').map { Key(text = it) } }
    val layer = rowStrings.mapIndexed { i, rowString ->
        if (i == rowStrings.size - 1) {
            val row = rowString.split(' ').map { compileKey(it, moveLayerKeys = moveLayerKeys, isOtherLayer = isOtherLayer) }.toMutableList()
            if (lowerLeftKey != null) row.add(0, lowerLeftKey)
            if (lowerRightKey != null) row.add(lowerRightKey)
            row
        } else if (decoupleRows.contains(i)) {
            val numberOfKeys = rowString.split(' ').size
            rowString.split(' ').map { compileKey(it, KeyWidth.FractionWidth(1F/numberOfKeys), moveLayerKeys, isOtherLayer) }
        } else {
            rowString.split(' ').map { compileKey(it, moveLayerKeys = moveLayerKeys, isOtherLayer = isOtherLayer) }
        }
    }.toMutableList()
    layer.add(listOf(bottomLeftKey) + bottomRow)
    return layer
}

fun compileKey(text: String, width: KeyWidth = KeyWidth.WeightWidth(1F), moveLayerKeys: List<String> = emptyList(), isOtherLayer: Boolean = false): Key {
    return if (text in moveLayerKeys) {
        Key(text = "", label = text, width = width, moveToLayer = 3 + moveLayerKeys.indexOf(text))
    } else if (isOtherLayer) Key(text = text, width = width, moveToLayer = 0)
    else Key(text = text, width = width)
}

fun layoutTogglesToSelectedLayoutNames(layoutToggles: List<Boolean>,
                                       allLayouts: List<CompiledLayout>): List<String> {
    return allLayouts.filterIndexed { i, _ -> layoutToggles[i] }.map { it.name }
}

fun selectedLayoutNamesToLayoutToggles(selectedLayoutNames: List<String>,
                                       allLayouts: List<CompiledLayout>): List<Boolean> {
    return allLayouts.map { selectedLayoutNames.contains(it.name) }
}

fun selectedLayoutNamesToSelectedLayouts(selectedLayoutNames: List<String>,
                                         allLayouts: List<CompiledLayout>): List<CompiledLayout> {
    return allLayouts.filter { selectedLayoutNames.contains(it.name) }
}