package com.imu.flowerdelivery.network.exceptions

import java.io.IOException

class ServerConnectionException : IOException() {
    override val message: String?
        get() = "Could not complete your request at the moment. Please try again later."
}