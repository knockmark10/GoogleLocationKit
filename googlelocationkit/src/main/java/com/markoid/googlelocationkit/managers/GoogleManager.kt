package com.markoid.googlelocationkit.managers

import android.content.Context
import com.google.android.gms.location.*
import com.markoid.googlelocationkit.callbacks.LocationServiceCallback
import com.markoid.googlelocationkit.di.Injector.providesGoogleManager
import com.markoid.googlelocationkit.entities.SettingsOptions
import com.markoid.googlelocationkit.utils.ApiChecker

class GoogleManager(
    private val mContext: Context,
    private val mOptions: SettingsOptions,
    private val mApiChecker: ApiChecker,
    private val mFusedClient: FusedLocationProviderClient,
    private val mSettings: SettingsClient
) : LocationCallback() {

    private var mListener: LocationServiceCallback? = null

    private val mLocationRequest: LocationRequest by lazy {
        LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = mOptions.updateInterval
            fastestInterval = mOptions.fastestInterval
        }
    }

    /**
     * Register listener to communicate.
     */
    fun registerListener(listener: LocationServiceCallback) {
        this.mListener = listener
    }

    /**
     * Request location updates to track user's location.
     */
    fun startLocationUpdates() {
        if (validate()) {
            val locationSettingsRequest = buildLocationSettingsRequest()
            this.mSettings.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener { setupLocation() }
                .addOnFailureListener { this.mListener?.onLocationServicesDisabled() }
        }
    }

    /**
     * Stop receiving location updates (turn off gps use).
     */
    fun stopLocationUpdates() {
        this.mFusedClient.removeLocationUpdates(this)
    }

    /**
     * Request user's last known location
     */
    fun getLastLocation() {
        if (validate()) {
            val locationClient = LocationServices.getFusedLocationProviderClient(mContext)
            locationClient.lastLocation
                .addOnSuccessListener { mListener?.onLocationHasChanged(it) }
                .addOnFailureListener { mListener?.onLocationHasChangedError(it) }
        }
    }

    private fun buildLocationSettingsRequest(): LocationSettingsRequest {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(this.mLocationRequest)
        return builder.build()
    }

    private fun notify(output: (listener: LocationServiceCallback) -> Unit): Boolean {
        this.mListener?.let(output)
        return false
    }

    /**
     * Validate if services are up and running, and permissions have been granted.
     */
    private fun validate(): Boolean = with(this.mApiChecker) {
        when {
            !this.areLocationServicesEnabled() -> notify { it.onLocationServicesDisabled() }
            !this.isLocationPermissionGranted() -> notify { it.onPermissionsAreMissing() }
            else -> true
        }
    }

    private fun setupLocation() {
        this.mFusedClient.requestLocationUpdates(this.mLocationRequest, this, this.mOptions.looper)
    }

    override fun onLocationResult(result: LocationResult?) {
        super.onLocationResult(result)
        result?.lastLocation?.let { this.mListener?.onLocationHasChanged(it) }
    }

    object Builder {

        private var updateInterval: Long = (15 * 1000).toLong()

        private var fastestInterval: Long = (10 * 1000).toLong()

        private var useLooper: Boolean = false

        fun setUpdateInterval(milliseconds: Long): Builder {
            this.updateInterval = milliseconds
            return this
        }

        fun setFastestInterval(milliseconds: Long): Builder {
            this.fastestInterval = milliseconds
            return this
        }

        fun useLooper(useLooper: Boolean): Builder {
            this.useLooper = useLooper
            return this
        }

        fun build(context: Context): GoogleManager =
            providesGoogleManager(context, encapsulateOptions())

        private fun encapsulateOptions(): SettingsOptions =
            SettingsOptions(this.updateInterval, this.fastestInterval, this.useLooper)

    }

}