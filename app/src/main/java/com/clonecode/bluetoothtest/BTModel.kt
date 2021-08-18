package com.clonecode.bluetoothtest

data class BTModel(
    var name: String = "",
    val mac: String = ""
) {
    companion object {
        fun invoke(
            name: String?,
            mac: String?
        ) = BTModel(
            name = name ?: "No Name",
            mac = mac ?: "No Address"
        )
    }
}
