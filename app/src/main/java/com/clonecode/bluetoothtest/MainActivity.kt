package com.clonecode.bluetoothtest

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.clonecode.bluetoothtest.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val TAG = "janghee"
    private lateinit var binding: ActivityMainBinding

    private val btViewModel: BTViewModel by viewModels()

    private val btAdapter: BTAdapter by lazy { BTAdapter() }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val mScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d(TAG, "onScanResult: ${result?.device?.address}")
            val bDevice = result?.device
            if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                bDevice?.let {
                    btViewModel.setBTDevice(BTModel.invoke(it.name, it.address))
                }
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    btViewModel.setBTDevice(BTModel.invoke(device?.name, device?.address))
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                }
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        setRecycler()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        binding.listener = View.OnClickListener {
            setBTPermission()
        }
        loadBTDevice()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        stopSearchBTDevices()
    }

    private fun loadBTDevice() {
        lifecycleScope.launch {
            btViewModel.getLiveBTDevice().observe(this@MainActivity) {
                btAdapter.submitList(it)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        showToast("위치권한을 추가해 주세요.")
                    }
                } else {
                    startSearchBTDevices()
                }
            }
        }
    }

    private fun setRecycler() {
        binding.btRecycler.apply {
            adapter = btAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val dpTop = pxToDp(12f)
                    val dpHorizontal = pxToDp(12f)
                    val dpBottom = pxToDp(12f)
                    if (parent.getChildAdapterPosition(view) == 0) {
                        outRect.set(dpHorizontal, dpTop, dpHorizontal, dpBottom)
                    } else {
                        outRect.set(dpHorizontal, 0, dpHorizontal, dpBottom)
                    }
                }
            })
        }
    }

    private fun pxToDp(px: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, this.resources.displayMetrics).toInt()

    private fun startSearchBTDevices() {
        if (bluetoothAdapter?.bluetoothLeScanner == null) return

        stopSearchBTDevices()

        bluetoothAdapter?.startDiscovery()
        bluetoothAdapter?.bluetoothLeScanner!!.startScan(mScanCallback)
        if (bluetoothAdapter == null) {
            showToast("블루투스를 지원하지 않습니다.")
            return
        }
        selectBTDevice()
    }

    private fun stopSearchBTDevices() {
        try {
            bluetoothAdapter?.cancelDiscovery()
            bluetoothAdapter?.bluetoothLeScanner!!.stopScan(mScanCallback)
        } catch (e: Exception) {
        }
    }

    private fun setBTPermission() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            startSearchBTDevices()
        }
    }

    private fun selectBTDevice() {
        // 이미 페어링 되어 있는 블루투스 기기
        val devices = bluetoothAdapter?.bondedDevices
        // 페어링된 디바이스의 크기 저장
        val pariedDeviceCount = devices?.size ?: 0
        // 페어링된 장치가 없는 경우
        if (pariedDeviceCount == 0) {
            // 페어링 함수 호출
        } else {
            val deviceList: MutableList<BTModel> = mutableListOf()
            devices?.forEach {
                deviceList.add(BTModel(it.name, it.address))
            }

//            btAdapter.submitList(deviceList)
        }
    }


    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (requestCode == RESULT_OK) {
                startSearchBTDevices()
            } else {
                setBTPermission()
            }
        }
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1000
    }
}