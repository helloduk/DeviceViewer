package com.solluzfa.solluzviewer.model

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class DataReaderURL : DataReader {

    companion object {
        const val TAG = "DataReaderURL"
    }

    private var dataAddress = URL("http://solluz.iptime.org/Data/MachineData2.txt");
    private var layoutAddress = URL("http://solluz.iptime.org/Data/MachineData2_Layout.txt");
    private var pushAddress = URL("http://solluz.iptime.org/Data/MachineData2_Push.txt");

    override fun readData(addressType: DataReader.AddressType): String {
        val source = when(addressType) {
            DataReader.AddressType.DATA -> dataAddress
            DataReader.AddressType.LAYOUT -> layoutAddress
            DataReader.AddressType.PUSH -> pushAddress
        }

        //Original file is encoded by "euc-kr"
        val reader = BufferedReader(InputStreamReader(source.openStream(), "euc-kr"))
        return reader.readLine()
    }

    override fun updateSetting(address: String, code: String) {
        dataAddress = URL((if (address.last() == '/') address else "$address/") + code + ".txt")
        layoutAddress = URL((if (address.last() == '/') address else "$address/") + code + "_Layout.txt")
        pushAddress = URL((if (address.last() == '/') address else "$address/") + code + "_Push.txt")
        Log.i(TAG, "setting updated : $dataAddress, $layoutAddress, $pushAddress")
    }
}