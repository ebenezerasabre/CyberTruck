package asabre.com.cybertruck.ui.fragments

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

object StartClass {
    private const val TAG = "StartClass"


    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var isConnected = false
    var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")




    fun initialize(): Boolean {
        Log.d(TAG, "initialize: bluetoothAdapter initialized")
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter == null){
            Log.d(TAG, "initialize: Unable to obtain a bluetoothAdapter")
            return false
        }
        return true
    }


    fun connect(address: String, context: Context): Boolean {
        Log.d(TAG, "connect: address -> $address")

        bluetoothAdapter?.let { bluetoothAdapter ->
            return try {
                Log.d(TAG, "connect: trying")
                val device = bluetoothAdapter.getRemoteDevice(address)

                bluetoothGatt = device.connectGatt(context, false, bluetoothGattCallback)
                Log.d(TAG, "the remote device connected ---> $device")
                true
            } catch (e: IllegalArgumentException){
                Log.d(TAG, "connect: Device not found with provided address.")
                false
            }
        } ?: kotlin.run {
            Log.d(TAG, "connect: BluetoothAdapter not initialized")
            return false
        }

    }


    fun writeCharacters(input: String){
//    fun writeCharacters(characteristic: BluetoothGattCharacteristic, payload: ByteArray){
//        if(isConnected){
//            var characteristic: BluetoothGattCharacteristic? = BluetoothGattCharacteristic(m_myUUID, )
//            bluetoothGatt?.writeCharacteristic("")
//        }


        val payload = input.toByteArray()
        bluetoothGatt?.let { bluetoothGatt ->
            val bluetoothGattService = StartClass.bluetoothGatt?.getService(m_myUUID)
        if(bluetoothGattService != null){
            val writeGattCharacteristic: BluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(m_myUUID)

            //            characteristic.writeType = writeType
//            characteristic.value = payload
//            bluetoothGatt.writeCharacteristic(characteristic)
//            bluetoothGatt.writeCharacteristic(writeGattCharacteristic)
            writeGattCharacteristic.setValue(payload)
            Log.d(TAG, "writeCharacters: called")
        }




        }


    }




    private val bluetoothGattCallback = object : BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            // successfully connected to the GATT Server

            val deviceAddress = gatt?.device?.address

            Log.d(TAG, "onConnectionStateChange: $deviceAddress")


            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.d(TAG, "onConnectionStateChange: connected")


                // successfully connected to the GATT Server
//                connectionState = BluetoothLeService.STATE_CONNECTED
//                broadcastUpdate(BluetoothLeService.ACTION_GATT_CONNECTED)
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.d(TAG, "onConnectionStateChange: disconnected")

                gatt?.close()

                // disconnected from the GATT Server
//                connectionState = BluetoothLeService.STATE_DISCONNECTED
//                broadcastUpdate(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            } else {
                gatt?.close()
            }
        }


        override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            Log.d(TAG, "onCharacteristicWrite: wrote on gatt")

        }
    }




}