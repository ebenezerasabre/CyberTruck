package asabre.com.cybertruck.ui.fragments

import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import asabre.com.cybertruck.R
import asabre.com.cybertruck.databinding.FragmentSettingsBinding
import asabre.com.cybertruck.other.ControlClass
import asabre.com.cybertruck.ui.viewModels.HomeViewModel
import asabre.com.cybertruck.ui.viewModels.SettingsViewModel
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import java.lang.Byte.decode
import java.lang.Long.decode


class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val TAG = "SettingsFragment"

    private lateinit var _binding: FragmentSettingsBinding
    private lateinit var controlClass: ControlClass

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        controlClass = ControlClass

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lightStateCallback()
        lightCirclesCallback()
        lightStateObservers()
        setSuspensions()
    }


    private fun lightStateCallback(){
        _binding.head1.setOnClickListener{ SettingsViewModel.headLight.postValue(1) }
        _binding.head2.setOnClickListener{ SettingsViewModel.headLight.postValue(0) }

        _binding.tail1.setOnClickListener{ SettingsViewModel.tailLight.postValue(1) }
        _binding.tail2.setOnClickListener{ SettingsViewModel.tailLight.postValue(0) }

        _binding.interior1.setOnClickListener{ SettingsViewModel.interior.postValue(1) }
        _binding.interior2.setOnClickListener{ SettingsViewModel.interior.postValue(0) }
    }

    private fun lightCirclesCallback(){
        _binding.headCircle.setOnClickListener { colorPicker("H", SettingsViewModel.headLight.value.toString()) }
        _binding.tailCircle.setOnClickListener { colorPicker("T", SettingsViewModel.tailLight.value.toString()) }
        _binding.interiorCircle.setOnClickListener { colorPicker("I", SettingsViewModel.interior.value.toString()) }
    }



    private fun lightStateObservers(){

        SettingsViewModel.headLight.observe(viewLifecycleOwner, {
            sendCommand(String.format("H${SettingsViewModel.headLight.value}${SettingsViewModel.headLightColorRGB.value}#"))
        })

        SettingsViewModel.tailLight.observe(viewLifecycleOwner, {
            sendCommand(String.format("T${SettingsViewModel.tailLight.value}${SettingsViewModel.tailLightColorRGB.value}#"))
        })

        SettingsViewModel.interior.observe(viewLifecycleOwner, {
            sendCommand(String.format("I${SettingsViewModel.interior.value}${SettingsViewModel.interiorLightColorRGB.value}#"))
        })
    }



    fun sendCommand(input: String){
        /**
         * connected
         */
//        controlClass.sendCommand(input)
    }


    private fun colorPicker(prefix: String, state: String){

        ColorPickerDialog
            .Builder(requireContext())
            .setTitle("Pick Theme")
            .setColorShape(ColorShape.CIRCLE)
            .setDefaultColor(R.color.purple_700)
            .setColorListener{color, colorHex ->
                // Handle Color Selection
                Log.d(TAG, "colorPicker: Selected color is $color")
                Log.d(TAG, "colorPicker: Selected color hex is $colorHex")
                                Log.d(TAG, "colorPicker: hexing " + parseColor(colorHex))
//                Log.d(TAG, "removed prefix " + colorHex.removePrefix("#"))

                val colorInt = parseColor(colorHex)
                val red: Int = red(colorInt) + 100
                val blue: Int = blue(colorInt) + 100
                val green: Int = green(colorInt) + 100
                val rgb: String = String.format("$red$blue$green")

                Log.d(TAG, "colorPicker: red " + red(colorInt))
                Log.d(TAG, "colorPicker: green " + green(colorInt))
                Log.d(TAG, "colorPicker: blue " + blue(colorInt))

                sendCommand(String.format("%s%s%s#", prefix, state, rgb))


                when(prefix){
                    "H" -> SettingsViewModel.headLightColorRGB.postValue(rgb)
                    "T" -> SettingsViewModel.tailLightColorRGB.postValue(rgb)
                    "I" -> SettingsViewModel.interiorLightColorRGB.postValue(rgb)
                }

            }
            .show()
    }



    private fun setSuspensions(){

        flClick()
        frClick()
        rlClick()
        rrClick()

    }


    private fun flClick(){
        _binding.fl.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            Log.d(TAG, "setSuspensions: called")
            when (view?.id) {
                R.id.fl -> {
                    if (motionEvent?.action == ACTION_DOWN) {
                        while (HomeViewModel.suspensionFlow.value == true){
                            Log.d(TAG, "onTouchDown ")
                            sendCommand(String.format("F1#"))
                        }
                    } else if (motionEvent?.action == ACTION_UP) {
                        HomeViewModel.suspensionFlow.postValue(false)
                        Log.d(TAG, "onTouchDown ")
                        sendCommand(String.format("F0#"))
                    }
                }
            }
            return@OnTouchListener true
        })
    }

    private fun frClick(){
        _binding.fr.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            Log.d(TAG, "setSuspensions: called")
            when (view?.id) {
                R.id.fr -> {
                    if (motionEvent?.action == ACTION_DOWN) {
                        while (HomeViewModel.suspensionFlow.value == true){
                            Log.d(TAG, "onTouchDown ")
                            sendCommand(String.format("F2#"))
                        }
                    } else if (motionEvent?.action == ACTION_UP) {
                        HomeViewModel.suspensionFlow.postValue(false)
                        Log.d(TAG, "onTouchDown ")
                        sendCommand(String.format("F0#"))
                    }
                }
            }
            return@OnTouchListener true
        })
    }

    private fun rlClick(){
        _binding.rl.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            Log.d(TAG, "setSuspensions: called")
            when (view?.id) {
                R.id.rl -> {
                    if (motionEvent?.action == ACTION_DOWN) {
                        while (HomeViewModel.suspensionFlow.value == true){
                            Log.d(TAG, "onTouchDown ")
                            sendCommand(String.format("R1#"))
                        }
                    } else if (motionEvent?.action == ACTION_UP) {
                        HomeViewModel.suspensionFlow.postValue(false)
                        Log.d(TAG, "onTouchDown ")
                        sendCommand(String.format("R0#"))
                    }
                }
            }
            return@OnTouchListener true
        })
    }

    private fun rrClick(){
        _binding.rr.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            Log.d(TAG, "setSuspensions: called")
            HomeViewModel.suspensionFlow.postValue(true)
            when (view?.id) {
                R.id.rr -> {
                    if (motionEvent?.action == ACTION_DOWN) {
                        while (HomeViewModel.suspensionFlow.value == true){
                            Log.d(TAG, "onTouchDown ")
                            sendCommand(String.format("R2#"))
                        }
                    } else if (motionEvent?.action == ACTION_UP) {
                        Log.d(TAG, "onTouchDown ")
                        HomeViewModel.suspensionFlow.postValue(false)
                        sendCommand(String.format("R0#"))
                    }
                }
            }
            return@OnTouchListener true
        })
    }


    private fun susPensionCall(prefix: String, action: MotionEvent?){
//        when(action?.action){
//            ACTION_DOWN -> {
//                Log.d(TAG, "susPensionCall: down called")
//                if (prefix.contentEquals("fl")) {
//                    sendCommand(String.format("F1#"))
//                }
//                if (prefix.contentEquals("fr")) {
//                    sendCommand(String.format("F2#"))
//                }
//
//                if (prefix.contentEquals("rl")) {
//                    sendCommand(String.format("R1#"))
//                }
//                if (prefix.contentEquals("rr")) {
//                    sendCommand(String.format("R2#"))
//                }
//            }
//            ACTION_UP -> {
//                Log.d(TAG, "susPensionCall: up called")
//                if (prefix.contentEquals("fl")) {
//                    sendCommand(String.format("F0#"))
//                }
//                if (prefix.contentEquals("fr")) {
//                    sendCommand(String.format("F0#"))
//                }
//
//                if (prefix.contentEquals("rl")) {
//                    sendCommand(String.format("R0#"))
//                }
//                if (prefix.contentEquals("rr")) {
//                    sendCommand(String.format("R0#"))
//                }
//            }
//        }
    }

}


//        new UI
//val source = getItem(p0) as DeviceModel
//deviceName.text = source.name
// launch sends max value
// brake under launch brake




//fun main() {
//
//    var string = "#ebenezer"
//    val char1 = '0'
//    val char2 = 'x'
//    val index = 0
//    val index1 = 1
//
//   val chars = string.toCharArray()
//    chars[index] = char1
//    chars[index1] = char2
//
//    string = String(chars)
//
//    println(string)
//}

// When you can do the things that I can but you don't
// and the bad things happen
// they happen because of you