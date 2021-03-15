package com.imu.flowerdelivery.network.exceptions

import java.io.IOException

class NoConnectivityException : IOException() {

    override val message: String?
        get() = "You seem to be offline. Please check your internet connection and try again.";
}