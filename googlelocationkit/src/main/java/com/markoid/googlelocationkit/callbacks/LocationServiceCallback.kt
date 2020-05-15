package com.markoid.googlelocationkit.callbacks

import android.location.Location

interface LocationServiceCallback {
    fun onLocationServicesDisabled()
    fun onPermissionsAreMissing()
    fun onLocationHasChanged(location: Location)
    fun onLocationHasChangedError(exception: Exception)
}