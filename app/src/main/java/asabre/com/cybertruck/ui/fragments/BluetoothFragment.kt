package asabre.com.cybertruck.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asabre.com.cybertruck.R
import asabre.com.cybertruck.databinding.FragmentBluetoothBinding
import asabre.com.cybertruck.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import asabre.com.cybertruck.other.Constants.REQUEST_ENABLE_BT
import asabre.com.cybertruck.other.ControlClass
import asabre.com.cybertruck.other.PermissionUtility
import asabre.com.cybertruck.ui.adapters.BluetoothDeviceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class BluetoothFragment : Fragment(R.layout.fragment_bluetooth), EasyPermissions.PermissionCallbacks {
    private  val TAG = "BluetoothFragment"

    private lateinit var _binding: FragmentBluetoothBinding
//    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var pairedDevice: Set<BluetoothDevice>

    private lateinit var controlClass: ControlClass
    private lateinit var showDialog: AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBluetoothBinding.inflate(layoutInflater)

        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        checkBluetooth()


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(!bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT)
        }
         else {
            pairedDeviceList()
        }
//        _binding.refreshPairedDevice.setOnClickListener{ pairedDeviceList() }



    }

    private fun pairedDeviceList()  {
        pairedDevice = bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if(pairedDevice.isNotEmpty()){
            for(device: BluetoothDevice in pairedDevice){
                list.add(device)
                Log.d(TAG, "device: $device")
            }
        } else {
            Log.d(TAG, "pairedDeviceList: no paired bluetooth devices found")
        }

        val adapter = BluetoothDeviceAdapter(requireContext(), list)
        _binding.listViewHolder.adapter = adapter
        _binding.listViewHolder.setOnItemClickListener { adapterView, view, i, l ->

            val device: BluetoothDevice = list[i]
            val address: String = device.address
            val deviceName: String = device.name


            Log.d(TAG, "pairedDeviceList: address $address name: $deviceName")


            // get battery
            setConnectionClass(device)


        }
    }


     private fun setConnectionClass(device: BluetoothDevice) = runBlocking {
         controlClass = ControlClass
//         controlClass.m_address = device.address
         controlClass.connectToDevice(device)
         trackProgressBarState()

         // get battery
        val theBat = getBattery(device)
         Log.d(TAG, "battery integer is $theBat")
    }

    private fun getBattery(pairedDevice: BluetoothDevice?): Int {
        Log.d(TAG, "getBattery: getting battery level")
        return pairedDevice?.let { bluetoothDevice ->
            (bluetoothDevice.javaClass.getMethod("getBatteryLevel"))
                .invoke(pairedDevice) as Int
        } ?: -1
    }


    private fun trackProgressBarState(){
        _binding.progressBarCyclic.visibility = View.VISIBLE
        controlClass.bluetoothDeviceConnected.observe(viewLifecycleOwner, {
//            if(it){
                findNavController().navigate(R.id.action_bluetoothFragment_to_homeFragment)
//            }
        })
    }





    private fun turnOffBluetooth(){
        bluetoothAdapter.disable()
        Log.d(TAG, "turnOffBluetooth: called")
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK){
                if(bluetoothAdapter!!.isEnabled){
                    Log.d(TAG, "onActivityResult: Bluetooth has been enable")
                } else {
                    Log.d(TAG, "onActivityResult: bluetooth has been disable")
                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED){
            Log.d(TAG, "onActivityResult: Bluetooth has been canceled")
        }

    }












    private fun getPairDevices() {
//        Log.d(TAG, "getPairDevices: sun getPairDevices: called")
//
////        Log.d(TAG, "gPairDevices before bonding: " + pairedDevice.size)
//        pairedDevice = bluetoothAdapter.bondedDevices
//        Log.d(TAG, "gPairDevices: after bonding" + pairedDevice.size)
//
//        val deviceLists = arrayListOf<DeviceModel>()
//
//        pairedDevice.forEach { device ->
//            val deviceName = device.name
//            val deviceHardwareAddress = device.address // MAC address
//            deviceLists.add(DeviceModel(deviceName, deviceHardwareAddress))
//            Log.d(TAG,"Paired device name $deviceName")
//        }
//
//        val bluetoothDeviceAdapter = BluetoothDeviceAdapter(requireContext(), deviceLists)
//        _binding.listViewHolder.adapter = bluetoothDeviceAdapter
//
//        var deviceModel: DeviceModel
//        _binding.listViewHolder.setOnItemClickListener { adapterView, view, i, l ->
//            deviceModel = adapterView.getItemAtPosition(i) as DeviceModel
//
//            Log.d(TAG, "Clicked DeviceModel is $deviceModel")
//        }

    }

//    fun clearPairedDevices(){
//        val bluetoothManager: BluetoothManager = context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//       for (devices: BluetoothDevice in pairedDevice){
//
//        }
//    }

//    fun onlyCyberDevices(): Set<BluetoothDevice>{
//        if(pairedDevice.isNotEmpty()){
//            return pairedDevice.filter { it.name.contains("CyberMINI") }.toSet()
//        }
//        return pairedDevice
//    }

//    override fun onStop() {
//        super.onStop()
//
//        bluetoothAdapter.disable()
//
//    }



//    override fun onDestroy() {
//        super.onDestroy()
////        unregisterReveiver(receiver)
//    }


















    private fun requestPermission(){
        if(PermissionUtility.hasBluetoothPermission(requireContext())){
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permissions to use this app",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
       if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
           AppSettingsDialog.Builder(this).build().show()
       } else { requestPermission() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }



//    fun setProgressDialog(context: Context, message:String): AlertDialog {
//        val llPadding = 30
//        val ll = LinearLayout(context)
//        ll.orientation = LinearLayout.HORIZONTAL
//        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
//        ll.gravity = Gravity.CENTER
//        var llParam = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT)
//        llParam.gravity = Gravity.CENTER
//        ll.layoutParams = llParam
//
//        val progressBar = ProgressBar(context)
//        progressBar.isIndeterminate = true
//        progressBar.setPadding(0, 0, llPadding, 0)
//        progressBar.layoutParams = llParam
//
//        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT)
//        llParam.gravity = Gravity.CENTER
//        val tvText = TextView(context)
//        tvText.text = message
//        tvText.setTextColor(Color.parseColor("#000000"))
//        tvText.textSize = 20.toFloat()
//        tvText.layoutParams = llParam
//
//        ll.addView(progressBar)
//        ll.addView(tvText)
//
//        val builder = AlertDialog.Builder(context)
//        builder.setCancelable(true)
//        builder.setView(ll)
//
//        val dialog = builder.create()
//        val window = dialog.window
//        if (window != null) {
//            val layoutParams = WindowManager.LayoutParams()
//            layoutParams.copyFrom(dialog.window?.attributes)
//            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
//            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
//            dialog.window?.attributes = layoutParams
//        }
//        Log.d(TAG, "setProgressDialog: alert on the way")
//        return dialog
//    }


}