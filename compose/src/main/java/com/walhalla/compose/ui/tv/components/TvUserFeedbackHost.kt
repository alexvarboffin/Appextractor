package com.walhalla.compose.ui.tv.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.walhalla.compose.feedback.ActionFeedbackBus
import com.walhalla.compose.feedback.UserFeedback
import com.walhalla.compose.viewmodel.ExtractorViewModel
import kotlinx.coroutines.delay

/**
 * Global TV toast — theme roles only (primary/error/secondary), no hardcoded RGB.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvUserFeedbackHost(
    viewModel: ExtractorViewModel,
    modifier: Modifier = Modifier
) {
    val feedback by ActionFeedbackBus.feedback.collectAsState()
    val progress by viewModel.extractionProgress.collectAsState()
    var visible by remember { mutableStateOf<UserFeedback?>(null) }
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(feedback) {
        val fb = feedback ?: return@LaunchedEffect
        visible = fb
        ActionFeedbackBus.clear()
        delay(3_200)
        if (visible == fb) visible = null
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            progress?.let { (name, pct) ->
                Text(
                    text = "$name · $pct%",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
            AnimatedVisibility(
                visible = visible != null,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 }
            ) {
                val fb = visible ?: return@AnimatedVisibility
                val (bg, fg) = when (fb.kind) {
                    UserFeedback.Kind.Success -> colors.primary to colors.onPrimary
                    UserFeedback.Kind.Error -> colors.error to colors.onError
                    UserFeedback.Kind.Info -> colors.secondary to colors.onSecondary
                }
                Text(
                    text = fb.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = fg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bg, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}
