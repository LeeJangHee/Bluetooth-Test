package com.clonecode.bluetoothtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BTViewModel: ViewModel() {

    private var _bDeviceList: MutableLiveData<List<BTModel>> = MutableLiveData()
    private val bDeviceList get() = _bDeviceList

    fun getBTDevice(bDeviceList: List<BTModel>) {
        _bDeviceList.postValue(bDeviceList)
    }

    fun getBTDevice(bDevice: BTModel) {
        val newDeviceList: MutableList<BTModel> = mutableListOf()
        _bDeviceList.value?.let {
            newDeviceList.addAll(it)
            if (!it.contains(bDevice)) {
                newDeviceList.add(bDevice)
                _bDeviceList.postValue(newDeviceList)
            }
        }
    }

    fun getLiveBTDevice(): LiveData<List<BTModel>> {
        return bDeviceList
    }
}