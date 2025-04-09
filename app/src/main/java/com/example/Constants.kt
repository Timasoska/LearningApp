package com.example

object Constants {
    // VERY IMPORTANT: Use 10.0.2.2 for Android Emulator to access host's localhost
    // If testing on a real device, use your computer's network IP address.
    // Make sure your Ktor server is running on port 8081 as per its application.conf
    const val BASE_URL = "http://10.0.2.2:8081"
}