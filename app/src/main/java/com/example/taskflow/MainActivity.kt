package com.example.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.taskflow.ui.navigation.AppRoot
import com.example.taskflow.ui.theme.TaskflowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskflowTheme {
                AppRoot()
            }
        }
    }
}
