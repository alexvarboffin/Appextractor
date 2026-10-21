package com.walhalla.compose.detail

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import java.util.Locale

/**
 * Case-insensitive highlight of all [query] occurrences (legacy AppVh.setAndHighlight).
 * Shared by phone [com.walhalla.compose.components.AppListItem] and the TV extractor row.
 */
fun highlightMatches(
    text: String,
    query: String,
    highlightColor: Color
): AnnotatedString {
    if (query.isEmpty()) {
        return AnnotatedString(text)
    }
    val lowerText = text.lowercase(Locale.getDefault())
    val lowerQuery = query.lowercase(Locale.getDefault())
    if (!lowerText.contains(lowerQuery)) {
        return AnnotatedString(text)
    }
    return buildAnnotatedString {
        var start = 0
        while (start < text.length) {
            val index = lowerText.indexOf(lowerQuery, start)
            if (index < 0) {
                append(text.substring(start))
                break
            }
            if (index > start) {
                append(text.substring(start, index))
            }
            withStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.Bold)) {
                append(text.substring(index, index + lowerQuery.length))
            }
            start = index + lowerQuery.length
        }
    }
}
