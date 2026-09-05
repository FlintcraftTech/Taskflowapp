package com.example.taskflow.ui.theme

import androidx.compose.ui.graphics.Color

// Taskflow's palette. Two jobs, and the second is the one that shaped the values: the app is
// built for a person who opens it at 1 AM (SPEC §Settings → Day begins at), so the dark scheme
// is a quiet near-black rather than a pure black with saturated accents, and the light scheme is
// an off-white rather than a bright white. Calm before contrast — but not below it: every
// on-colour here sits above the 4.5:1 body-text ratio against its own surface.
//
// The hue is a muted blue-green: present enough to make a selected date tile or a checked box
// obvious, quiet enough that a list of tasks does not read as a list of alerts.

// Light
val LightPrimary = Color(0xFF31606E)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFB7E9FB)
val LightOnPrimaryContainer = Color(0xFF001F28)
val LightSecondary = Color(0xFF4C616B)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFCFE6F1)
val LightOnSecondaryContainer = Color(0xFF071E26)
val LightBackground = Color(0xFFF7FAFC)
val LightOnBackground = Color(0xFF191C1D)
val LightSurface = Color(0xFFF7FAFC)
val LightOnSurface = Color(0xFF191C1D)
val LightSurfaceVariant = Color(0xFFDBE4E8)
val LightOnSurfaceVariant = Color(0xFF3F484B)
val LightOutline = Color(0xFF6F797C)
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

// Dark
val DarkPrimary = Color(0xFF8ACFE2)
val DarkOnPrimary = Color(0xFF003543)
val DarkPrimaryContainer = Color(0xFF164D5A)
val DarkOnPrimaryContainer = Color(0xFFB7E9FB)
val DarkSecondary = Color(0xFFB3CAD5)
val DarkOnSecondary = Color(0xFF1E333C)
val DarkSecondaryContainer = Color(0xFF344A53)
val DarkOnSecondaryContainer = Color(0xFFCFE6F1)
val DarkBackground = Color(0xFF191C1D)
val DarkOnBackground = Color(0xFFE1E3E4)
val DarkSurface = Color(0xFF191C1D)
val DarkOnSurface = Color(0xFFE1E3E4)
val DarkSurfaceVariant = Color(0xFF3F484B)
val DarkOnSurfaceVariant = Color(0xFFBFC8CC)
val DarkOutline = Color(0xFF899296)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)
