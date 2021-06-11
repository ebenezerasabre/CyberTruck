package asabre.com.cybertruck.other

import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import asabre.com.cybertruck.ui.viewModels.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*
import java.util.logging.Handler

object ControlClass {
    private const val TAG = "ControlClass"


//
    var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    var m_bluetoothSocket: BluetoothSocket? = null
    lateinit var m_bluetoothAdapter: BluetoothAdapter
    var m_isConnected: Boolean = false
//    lateinit var m_address: String
    private var connectSuccess: Boolean = true

    var progressBarState = MutableLiveData<Boolean>()

    var bluetoothDeviceConnected = MutableLiveData<Boolean>()

    private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

    var batteryPercentage = MutableLiveData<String>()

    private val handler: Handler
        get() {
            TODO()
        }


    fun sendCommand(input: String) {
         Log.d(TAG, "input String: $input")
             if(m_bluetoothSocket != null){
                 try {
                     m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                 } catch (e: IOException){
                     e.printStackTrace()
                 }
             }
    }

    private  fun readIncoming() {

            Log.d(TAG, "reaIncoming: ")
//            var numBytes: Int // bytes returned from read()
//            while (true){
//                // read from the inputStream
//                numBytes = try {
//                    m_bluetoothSocket!!.inputStream.read(mmBuffer)
//                } catch (e: IOException){
//                    Log.d(TAG, "Input stream was disconnected")
//                    break
//                }
//                HomeViewModel.batteryPercentage.postValue(String(mmBuffer, 0, numBytes))
//            }

        if(m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.inputStream.read(mmBuffer)
            } catch (e: IOException){
                Log.d(TAG, "Input stream was disconnected")
            }
            HomeViewModel.batteryPercentage.postValue(String(mmBuffer))
        }


    }

     fun disconnect(){
        try {
            m_bluetoothSocket!!.close()
            m_bluetoothSocket = null
            m_isConnected = false
        } catch (e: IOException){
            e.printStackTrace()
        }
    }


     fun connectToDevice(device: BluetoothDevice) = runBlocking {
         device.createRfcommSocketToServiceRecord(m_myUUID)

         Log.d(TAG, "connectToDevice: called")
        launch {
//            progressBarState.postValue(true)
            try {
                Log.d(TAG, "connectToDevice: trying one")
                if(m_bluetoothSocket == null || !m_isConnected){
                    Log.d(TAG, "connectToDevice: trying two")
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
//                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)

                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
//                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothAdapter.cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            } catch (e: IOException){
                connectSuccess = false
                Log.d(TAG, "connectToDevice: IOException ${e.message}" )
                Log.d(TAG, "connectToDevice: trace ${e.printStackTrace()}")
            }
        }

        launch {
            if(!connectSuccess){
                Log.d(TAG, "connectToDevice: couldn't connect")
            } else {
                m_isConnected = true
                bluetoothDeviceConnected.postValue(true)
                Log.d(TAG, "connectToDevice: connected successfully")

                // read inputStream
//                readIncoming()
            }
            progressBarState.postValue(false)

        }

             if(connectSuccess){
//                 readIncoming()
             }

    }




}