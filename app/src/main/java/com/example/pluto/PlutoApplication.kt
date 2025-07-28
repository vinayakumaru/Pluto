package com.example.pluto

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The Application class, required by Hilt to trigger code generation.
 * This class serves as the application-level dependency container.
 */
@HiltAndroidApp
class PlutoApplication : Application()