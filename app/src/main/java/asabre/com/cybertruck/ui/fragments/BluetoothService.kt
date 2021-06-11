package asabre.com.cybertruck.ui.fragments

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.STATE_CONNECTED
import android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.lang.IllegalArgumentException

class BluetoothLeService : Service() {
    private val TAG = "BluetoothService"
    private val binder = LocalBinder()
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder: Binder(){
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    fun initialize(): Boolean {
        Log.d(TAG, "initialize: bluetoothAdapter initialized")
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null){
            Log.d(TAG, "initialize: Unable to obtain a bluetoothAdapter")
            return false
        }
        return true
    }

    fun connect(address: String): Boolean {

//        Log.d(TAG, "connect: called")
//        bluetoothAdapter?.let { bluetoothAdapter ->
//            try {
//                val device = bluetoothAdapter.getRemoteDevice(address)
//
//                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
//                Log.d(TAG, "the remote device connected ---> $device")
//                return true
//            } catch (e: IllegalArgumentException){
//                Log.d(TAG, "connect: Device not found with provided address.")
//                return false
//            }
//        } ?: kotlin.run {
//            Log.d(TAG, "connect: BluetoothAdapter not initialized")
//            return false
//        }

        return true

    }

    private val bluetoothGattCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            // successfully connected to the GATT Server

            val deviceAddress = gatt?.device?.address



            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.d(TAG, "onConnectionStateChange: connected")


                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.d(TAG, "onConnectionStateChange: disconnected")

                gatt?.close()

                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            } else {
                gatt?.close()
            }
        }
    }

    private fun broadcastUpdate(action: String){
        val thisIntent = Intent(action)
        sendBroadcast(thisIntent)
    }
//
//
    companion object {
        const val ACTION_GATT_CONNECTED =
                "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
                "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2

    }

}