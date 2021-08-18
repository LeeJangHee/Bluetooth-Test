package com.clonecode.bluetoothtest

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.clonecode.bluetoothtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val btAdapter: BTAdapter by lazy { BTAdapter() }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecycler()

        binding.listener = View.OnClickListener {
            startConnectBT()
        }
    }

    private fun setRecycler() {
        binding.btRecycler.apply {
            adapter = btAdapter.apply {
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        val dpTop = pxToDp(12f)
                        val dpHorizontal = pxToDp(12f)
                        val dpBottom = pxToDp(12f)
                        if (state.itemCount == 0) {
                            outRect.set(dpHorizontal, dpTop, dpHorizontal, dpBottom)
                        } else {
                            outRect.set(dpHorizontal, 0, dpHorizontal, dpBottom)
                        }
                    }
                }
            }
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
            selectBTDevice()
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

            btAdapter.submitList(deviceList)
        }
    }



    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (requestCode == RESULT_OK) {
                selectBTDevice()
            } else {
                setBTPermission()
            }
        }
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1000
    }
}