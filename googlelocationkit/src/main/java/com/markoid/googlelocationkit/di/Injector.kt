package com.markoid.googlelocationkit.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.markoid.googlelocationkit.entities.SettingsOptions
import com.markoid.googlelocationkit.managers.GoogleManager
import com.markoid.googlelocationkit.utils.ApiChecker

object Injector {

    fun providesGoogleManager(context: Context, options: SettingsOptions): GoogleManager =
        GoogleManager(
            context,
            options,
            providesApiChecker(context),
            providesFusedClient(context),
            providesSettingsClient(context)
        )

    private fun providesApiChecker(context: Context): ApiChecker =
        ApiChecker(context)

    fun providesFusedClient(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun providesSettingsClient(context: Context): SettingsClient =
        LocationServices.getSettingsClient(context)

}