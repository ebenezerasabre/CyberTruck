package asabre.com.cybertruck.ui.fragments

//import androidx.appcompat.widget.AppCompatSeekBar
import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.KeyEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import asabre.com.cybertruck.R
import asabre.com.cybertruck.databinding.FragmentHomeBinding
import asabre.com.cybertruck.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import asabre.com.cybertruck.other.ControlClass
import asabre.com.cybertruck.other.PermissionUtility
import asabre.com.cybertruck.ui.viewModels.HomeViewModel
import com.github.dhaval2404.colorpicker.util.setVisibility
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.log

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val TAG = "HomeFragment"

    private lateinit var _binding: FragmentHomeBinding
//    private lateinit var controlClass: ControlClass


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        return _binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        pageNavigation()
        connectedIndicator()
        callbacks()
        subscribeObservers()

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


    private fun pageNavigation(){
        _binding.settings.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        _binding.bluetooth.setOnClickListener{
//            findNavController().navigate(R.id.action_homeFragment_to_bluetoothFragment)
//            findNavController().navigate(R.id.action_homeFragment_to_bleDevicesFragment)
            findNavController().navigate(R.id.action_homeFragment_to_scanFragment)
        }
    }

    private fun connectedIndicator(){
        /** connected */

//        controlClass = ControlClass
////        controlClass.connectToDevice()
//        controlClass.bluetoothDeviceConnected.observe(viewLifecycleOwner, {
//            if(it){
//                _binding.tvConnected.text = String.format("connected")
//            }
//        })


    }


    private fun callbacks(){
        driverModeCallback()
        driverGearCallback()
        leftSeekBar()
        rightSeekBar()
        ludacrisLaunch()
        setBreak()
        setBatteryImage()
    }



    private fun leftSeekBar(){
        var currentValue: Int = 0
        _binding.leftSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                currentValue = mapRange(p1, IntRange(0, 100), IntRange(750, 2250))
                _binding.seekBarLeftText.text = currentValue.toString()
                HomeViewModel.steerProgress.postValue(currentValue)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                _binding.leftSeekBar.progress = 50
            }
        })
    }

    private fun rightSeekBar(){
        var currentValue = 0
        _binding.rightSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                currentValue = mapRange(p1, IntRange(0, 100), IntRange(0, 255))
                _binding.seekBarRightText.text = currentValue.toString()
                HomeViewModel.driveProgress.postValue(currentValue)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                _binding.rightSeekBar.progress = 50
            }
        })
    }


    //  0 - 100

    fun mapRange(number: Int, prevRange: IntRange, newRange: IntRange) : Int {
        val ratio = number.toFloat() / (prevRange.last - prevRange.first)
        return (ratio * (newRange.last - newRange.first) + newRange.first).toInt()
    }

    private fun driverModeCallback(){
        _binding.buttonNormal.setOnClickListener{
            HomeViewModel.driveMode.postValue(0)
            _binding.rightSeekBar.visibility = View.VISIBLE
            _binding.seekBarRightText.visibility = View.VISIBLE
            _binding.ludacrisLaunch.visibility = View.GONE

        }
        _binding.buttonLudacris.setOnClickListener {
            HomeViewModel.driveMode.postValue(1)
            _binding.rightSeekBar.visibility = View.GONE
            _binding.seekBarRightText.visibility = View.GONE
            _binding.ludacrisLaunch.visibility = View.VISIBLE

        }
    }

    private fun ludacrisLaunch(){
        _binding.ludacrisLaunch.setOnClickListener{
            HomeViewModel.driveGear.postValue(255)
        }
    }



    private fun setBreak() {
        _binding.breakButton.setOnTouchListener(View.OnTouchListener {view, motionEvent ->
            Log.d(TAG, "onTouch: called")
            when(view?.id){
                R.id.breakButton -> {
                    if (motionEvent?.action == ACTION_DOWN) {
                        Log.d(TAG, "onTouch: down")
                        sendCommand("B1#")
                    } else if (motionEvent?.action == ACTION_UP) {
                        Log.d(TAG, "onTouch: up")
                        sendCommand("B0#")
                    }
                }
            }
            return@OnTouchListener true
        })
    }



    private fun driverGearCallback(){
        _binding.segmentR.setOnClickListener { HomeViewModel.driveGear.postValue(0) }
        _binding.segmentD.setOnClickListener { HomeViewModel.driveGear.postValue(1) }
        _binding.segmentP.setOnClickListener { HomeViewModel.driveGear.postValue(2) }

    }


    private fun subscribeObservers()  {

        HomeViewModel.driveMode.observe(viewLifecycleOwner, {
            sendCommand(String.format("D%s%s225#", HomeViewModel.driveGear.value, HomeViewModel.driveMode.value))
        })

        HomeViewModel.driveGear.observe(viewLifecycleOwner, {
            sendCommand(String.format("D%s%s225#", HomeViewModel.driveGear.value, HomeViewModel.driveMode.value))
        })

        HomeViewModel.driveProgress.observe(viewLifecycleOwner, {
            sendCommand(String.format("D%s%s%s#", HomeViewModel.driveGear.value, HomeViewModel.driveMode.value, HomeViewModel.driveProgress.value ))
        })

        HomeViewModel.steerProgress.observe(viewLifecycleOwner, {
            sendCommand(String.format("S%s#", HomeViewModel.steerProgress.value))
        })

    }

    private fun sendCommand(input: String){
//        controlClass.sendCommand(input)

        Log.d(TAG, "sendCommand: with string $input")
        StartClass.writeCharacters(input)


    }


    //  drive geer ... r d p
    // right slider driver progress
    // left slider steer_progress




    private fun setBatteryImage(){
        HomeViewModel.batteryPercentage.observe(viewLifecycleOwner, {
            when(HomeViewModel.batteryPercentage.value?.toInt()) {
                in 0..24 -> {_binding.batteryImg.setImageResource(R.drawable.battery20)}
                in 25..54 -> {_binding.batteryImg.setImageResource(R.drawable.battery30)}
                in 55..64 -> {_binding.batteryImg.setImageResource(R.drawable.battery50)}
                in 65..84 -> {_binding.batteryImg.setImageResource(R.drawable.battery70)}
                in 85..94 -> {_binding.batteryImg.setImageResource(R.drawable.battery90)}
                in 95..100 -> {_binding.batteryImg.setImageResource(R.drawable.battery100)}
            }
        })
    }

// > 94 100
//    94 - 85 90
//  84 -65 - 70
//    64 - 55  50
    //  54 - 25  30
    // 24 - 0 20
//




}