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


private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2

class BluetoothFragment : Fragment(R.layout.fragment_bluetooth), EasyPermissions.PermissionCallbacks {
    private  val TAG = "BluetoothFragment"



    private lateinit var _binding: FragmentBluetoothBinding
//    private lateinit var bluetoothManager: BluetoothManager
//    private lateinit var bluetoothAdapter: BluetoothAdapter
//    private lateinit var pairedDevice: Set<BluetoothDevice>
//
//    private lateinit var controlClass: ControlClass
//    private lateinit var showDialog: AlertDialog


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



    }

    private fun pairedDeviceList()  {


//        val adapter = BluetoothDeviceAdapter(requireContext(), list)
//        _binding.listViewHolder.adapter = adapter
//        _binding.listViewHolder.setOnItemClickListener { adapterView, view, i, l ->
//
//            val device: BluetoothDevice = list[i]
//            val address: String = device.address
//            val deviceName: String = device.name
//
//
//            Log.d(TAG, "pairedDeviceList: address $address name: $deviceName")


    }



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





}