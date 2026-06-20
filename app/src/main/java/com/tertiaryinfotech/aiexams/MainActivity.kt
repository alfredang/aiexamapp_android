package com.tertiaryinfotech.aiexams

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tertiaryinfotech.aiexams.ui.AppRoot
import com.tertiaryinfotech.aiexams.ui.SessionViewModel
import com.tertiaryinfotech.aiexams.ui.theme.AIExamsTheme

class MainActivity : ComponentActivity() {

    private val session: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AIExamsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (session.isReady) {
                        AppRoot(session)
                    } else {
                        Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                    }
                }
            }
        }
    }
}
