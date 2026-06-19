package cc.neo.sdkcall.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.neo.sdkcall.libs.DtmfTonePlayer

@Composable
fun DialPad(
    modifier: Modifier = Modifier,
    onKeyPress: (String) -> Unit
) {

    val dtmfPlayer = remember { DtmfTonePlayer() }

    DisposableEffect(Unit) {
        onDispose {
            dtmfPlayer.release()
        }
    }

    val keys = listOf(
        "1","2","3",
        "4","5","6",
        "7","8","9",
        "*","0","#"
    )

    var input by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (input.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = input,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    color = androidx.compose.ui.graphics.Color.White,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { input = input.dropLast(1) },
                    modifier = Modifier
                        .size(32.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = { input = "" })
                        }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Delete",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        /* ===== DIALPAD GRID ===== */
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            val rows = keys.chunked(3)
            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (key in row) {
                        DialPadButton(
                            text = key,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                input += key
                                onKeyPress(key)
                                dtmfPlayer.play(key)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialPadButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(4.dp)
            .height(48.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.15f))
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}
