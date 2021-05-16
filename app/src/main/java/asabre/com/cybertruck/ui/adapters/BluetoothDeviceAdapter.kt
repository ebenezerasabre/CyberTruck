package asabre.com.cybertruck.ui.adapters

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import asabre.com.cybertruck.R
import asabre.com.cybertruck.other.DeviceModel

class BluetoothDeviceAdapter(private val context: Context, private val dataSource: ArrayList<BluetoothDevice>) : BaseAdapter() {


    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(p0: Int): Any {

        return dataSource[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var convertView = p1
        convertView = LayoutInflater.from(context).inflate(R.layout.browse_devices, p2, false)

        val deviceName = convertView.findViewById<TextView>(R.id.device_name)
        val deviceImage = convertView.findViewById<ImageView>(R.id.device_image)


//        new UI
//val source = getItem(p0) as DeviceModel
//deviceName.text = source.name
// launch sends max value
// brake under launch brake



        val device = getItem(p0) as BluetoothDevice
        deviceName.text = device.name


    return convertView
    }

    // us university class B
    // nofork university
    //  dear proffessor i have a bachelors in electrical
    //  i see my research area alines with my projects
    //


}


//  f2# f0#
//  f1# f0#







