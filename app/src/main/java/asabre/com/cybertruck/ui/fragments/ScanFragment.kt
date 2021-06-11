package asabre.com.cybertruck.ui.fragments

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.*
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import asabre.com.cybertruck.R
import asabre.com.cybertruck.databinding.FragmentBluetoothBinding
import asabre.com.cybertruck.databinding.ScanFragmentBinding
import asabre.com.cybertruck.other.Constants
import asabre.com.cybertruck.other.ControlClass
import asabre.com.cybertruck.other.PermissionUtility
import asabre.com.cybertruck.ui.adapters.BluetoothDeviceAdapter
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

/**
 * This code from android documentation
 * https://developer.android.com/guide/topics/connectivity/bluetooth/connect-gatt-server
 */

class ScanFragment: Fragment(R.layout.scan_fragment), EasyPermissions.PermissionCallbacks{
    private val TAG = "ScanFragment"
    private lateinit var _binding: ScanFragmentBinding


    private var scanning = false
    private var SCAN_PERIOD: Long = 10000
    private val handler = Handler()
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private lateinit var locationManager: LocationManager
    var myIntent: Intent? = null

    private lateinit var scannedDevices: Set<BluetoothDevice>
    private lateinit var deviceList: ArrayList<BluetoothDevice>

    private var bluetoothLeService: BluetoothLeService? = null
    private var connected: Boolean? = null

//    private lateinit var controlClass: ControlClass



    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = ScanFragmentBinding.inflate(layoutInflater)
        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        requestPermission()

        // set control class
//        controlClass = ControlClass

//        _binding.startScanning.setOnClickListener {
//            startBleScan()
//        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart()")
        super.onStart()
        turnOnGps()
        init()
//        bluetoothLeService?.initialize()
        StartClass.initialize()
        scanLeDevice()
    }


    override fun onStop() {
        super.onStop()

    }


    private fun init(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        deviceList = ArrayList()
    }


    private fun scanLeDevice(){
        if(!scanning){
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }



    private val leScanCallback: ScanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            val bluetoothDevice: BluetoothDevice? = result?.device
                Log.d(TAG, "onScanResult: address ---> ${bluetoothDevice?.address} ; name ---> ${bluetoothDevice?.name}")

            if (bluetoothDevice != null) {
                    if(!deviceList.contains(bluetoothDevice)){
                        deviceList.add(bluetoothDevice)
                        showScannedDevices(deviceList)
                    }
            }


        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.d(TAG, "onBatchScanResults: called")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(TAG, "onScanFailed: called")
        }
    }

    private fun showScannedDevices(list: ArrayList<BluetoothDevice>){
        if(context != null){
            val adapter = BluetoothDeviceAdapter(requireContext(), list)
            _binding.listViewHolderScan.adapter = adapter
            _binding.listViewHolderScan.setOnItemClickListener { adapterView, view, i, l ->
                val device: BluetoothDevice = list[i]
                Log.d(TAG, "Device clicked $device")

//                bluetoothLeService?.connect(device.address)

                StartClass.connect(device.address, requireContext())



            }
        }
    }


    override fun onResume() {
        super.onResume()
        if(!bluetoothAdapter.isEnabled){
            promptEnableBluetooth()
        }
    }

    private fun promptEnableBluetooth(){
        if(!bluetoothAdapter.isEnabled){
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_ENABLE_BT -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    private fun startBleScan(){
        Log.d(TAG, "startBleScan: called")
        requestPermission()

    }



    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsGranted: called")

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            AppSettingsDialog.Builder(this).build().show()
        } else { requestPermission() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    private fun requestPermission(){
//        if(PermissionUtility.hasBluetoothPermission(requireContext())){
//            Log.d(TAG, "requestPermission: called")
//            return
//        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app",
                    Constants.REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app",
                    Constants.REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    private fun gpsEnabled(): Boolean {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun turnOnGps(){
        if(!gpsEnabled()){
            myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
    }


//     code to manage service lifecycle
//    private val serviceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
//            bluetoothLeService = (service as BluetoothLeService.LocalBinder).getService()
//            bluetoothLeService?.let { bluetoothLeService ->
//                // call functions on service to check connection and connect to device
//                if(!bluetoothLeService.initialize()){
//                    Log.d(TAG, "onServiceConnected: Unable to initialize bluetooth")
//                    // go back to homeFragment if you will
//                }
//                // perform device connection
//                Log.d(TAG, "onServiceConnected: called")
//                bluetoothLeService.connect("F8:04:2E:F9:2E:73")  // add bluetoothDevice address
//
//            }
//        }
//
//        override fun onServiceDisconnected(componentName: ComponentName?) {
//            bluetoothLeService = null
//            Log.d(TAG, "onServiceDisconnected: called")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: called")


        // bluetoothService
//        val gattServiceIntent = Intent(context, BluetoothLeService::class.java)
//        context?.bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

    }



//    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver(){
//        override fun onReceive(p0: Context?, p1: Intent?) {
//            if (p1 != null) {
//                when(p1.action){
//                    BluetoothLeService.ACTION_GATT_CONNECTED -> {
//                        connected = true
//                    }
//                    BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
//                        connected = false
//                    }
//                }
//            }
//        }
//    }
//
//
//    override fun onPause() {
//        super.onPause()
//
//    }
//
//    private fun makeGattUpdateIntentFilter(): IntentFilter? {
//        return IntentFilter().apply {
//            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
//            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
//        }
//    }


}