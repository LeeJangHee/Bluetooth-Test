package com.clonecode.bluetoothtest

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.RecyclerView
import com.clonecode.bluetoothtest.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val TAG = "janghee"
    private lateinit var binding: ActivityMainBinding

    private var mDeviceList: ObservableArrayList<BTModel> = ObservableArrayList()
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
                    btViewModel.getBTDevice(BTModel(it.name, it.address))
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
            when(intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let { mDeviceList.add(BTModel(it.name, it.address)) }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {  }
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        setRecycler()

        binding.listener = View.OnClickListener {
            startConnectBT()
        }

        btViewModel.getLiveBTDevice().observe(this) {
            btAdapter.submitList(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        stopSearchBTDevices()
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

    private fun startConnectBT() {
        if (bluetoothAdapter == null) {
            showToast("블루투스를 지원하지 않습니다.")
            return
        }
        setBTPermission()
    }

    private fun setBTPermission() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            startSearchBTDevices()
        }
    }

    private fun startSearchBTDevices() {
        if (bluetoothAdapter?.bluetoothLeScanner == null) return

        stopSearchBTDevices()

        bluetoothAdapter?.bluetoothLeScanner!!.startScan(mScanCallback)
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

    private fun stopSearchBTDevices() {
        try {
            bluetoothAdapter?.bluetoothLeScanner!!.stopScan(mScanCallback)
        } catch (e: Exception) {}
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