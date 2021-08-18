package com.clonecode.bluetoothtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BTViewModel: ViewModel() {

    private var _bDeviceList: MutableLiveData<List<BTModel>> = MutableLiveData()
    private val bDeviceList get() = _bDeviceList

    fun setBTDevice(bDeviceList: List<BTModel>) {
        _bDeviceList.value = bDeviceList
    }

    fun setBTDevice(bDevice: BTModel) {
        val devices: MutableList<BTModel> = mutableListOf()
        devices.addAll(getBTDeviceList())
        if (!devices.contains(bDevice)) {
            devices.add(bDevice)
            setBTDevice(devices)
        }
    }

    fun getBTDevice(index: Int): BTModel {
        return bDeviceList.value?.get(index) ?: BTModel()
    }

    fun getBTDeviceList(): List<BTModel> {
        return bDeviceList.value ?: listOf()
    }

    fun getLiveBTDevice(): LiveData<List<BTModel>> {
        return bDeviceList
    }
}