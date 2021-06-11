package asabre.com.cybertruck.ui.viewModels

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import asabre.com.cybertruck.other.ControlClass
import asabre.com.cybertruck.other.DeviceModel

object HomeViewModel : ViewModel()  {

    var isShowingDetailView = MutableLiveData<Boolean>(false)
    var isShowingSettingsView = MutableLiveData<Boolean>(false)

    var speed = MutableLiveData<Double>(50.0)
    var isEditing = MutableLiveData<Boolean>(false)

    var steerProgress = MutableLiveData<Int>(90)
    var driveProgress = MutableLiveData<Int>(90)
    var driveMode = MutableLiveData<Int>(0)
    var driveGear = MutableLiveData<Int>(0)
    var brakeVariable = MutableLiveData(0)
    var luda = MutableLiveData<Boolean>(false)
    var batteryPercentage = MutableLiveData<String>()
    var suspensionFlow = MutableLiveData<Boolean>(false)


    fun mapRange(number: Int, prevRange: IntRange, newRange: IntRange) : Int {
        val ratio = number.toFloat() / (prevRange.last - prevRange.first)
        return (ratio * (newRange.last - newRange.first) + newRange.first).toInt()
    }



//
//    lateinit var deviceModel: MutableLiveData<DeviceModel>
//    var connected = MutableLiveData<Boolean>()
//    var colorRgb = MutableLiveData<String>()  // rgb values
//    var driveMode = MutableLiveData<String>() // normal, ludacris
//    var driveDirection = MutableLiveData<String>() // R, D, P
//    var blueToothConnected = MutableLiveData<Boolean>()
//    var connectedBluetoothDevice = MutableLiveData<BluetoothDevice>() // selected bluetooth device
//
//    lateinit var colorButtonSelect: String
//
//    private lateinit var controlClass: ControlClass
//
//    private fun setViewModel(){
//
//    }
//
//
//
//    fun getDeviceModel(): LiveData<DeviceModel>{
//        return deviceModel
//    }
//
//    fun getConnection(): LiveData<Boolean>{
//        return connected
//    }
//
//    fun getColorRGB(): LiveData<String>{
//        return colorRgb
//    }
//
//    fun getBluetoothConnected(): LiveData<Boolean>{
//        return blueToothConnected
//    }
//    fun setBluetoothDevice(){
//        connected.postValue(true)
//    }


    // 20/100
    //  ( ratio * (2250 - 750) ) + 750
    // ratio * 255



/// b0# b1#
// f1# f0#,,,,,,  f2# f0#
// r f  , 0, 1
// min .. f 1 2



// right//   0 - 255
// slider driver release turns to 0

// left   750 - 2250
// returns middle
// driver

// drive mode  normal  ludacris

// at ludacris mode remove slider bring button with launch
// when lauch is clicked send 255


 // brake variable



}

fun main(){
    // find rationOfOldRange

    val prevRange: IntRange = IntRange(0, 100)
    val newRange: IntRange = IntRange(750, 2250)
    val num = 100
    fun mapRange(number: Int, prevRange: IntRange, newRange: IntRange) : Int {
        val ratio = number.toFloat() / (prevRange.last - prevRange.first)
        return (ratio * (newRange.last - newRange.first) + newRange.first).toInt()
    }

    println(mapRange(num, prevRange, newRange))
}