package com.clonecode.bluetoothtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BTViewModel: ViewModel() {

    private var _bDeviceList: MutableLiveData<List<BTModel>> = MutableLiveData()
    private val bDeviceList get() = _bDeviceList

    fun setBTDevice(bDeviceList: List<BTModel>) {
        _bDeviceList.postValue(bDeviceList)
    }

    fun setBTDevice(bDevice: BTModel) {
        val newBTDeviceList: MutableList<BTModel> = mutableListOf()
        newBTDeviceList.addAll(getBTDeviceList())
        _bDeviceList.value?.let {
            if (!newBTDeviceList.contains(bDevice)) {
                newBTDeviceList.add(bDevice)
                _bDeviceList.postValue(newBTDeviceList)
            }
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