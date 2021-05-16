package asabre.com.cybertruck.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object SettingsViewModel : ViewModel() {

    var headLight = MutableLiveData(0)
    var tailLight = MutableLiveData(0)
    var interior = MutableLiveData(0)

    var headLightColorRGB = MutableLiveData<String>("255255255")
    var tailLightColorRGB = MutableLiveData<String>("255255255")
    var interiorLightColorRGB = MutableLiveData<String>("255255255")

    var suspension = MutableLiveData<Int>(0)




    fun sendSuspension(){

    }





 }