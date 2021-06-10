package com.solluzfa.solluzviewer.model

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import android.widget.Toast
import com.solluzfa.solluzviewer.SolluzApplication
import java.util.*


@SuppressLint("NewApi")
class DataReaderBluetooth : DataReader, ScanCallback() {

    companion object {
        // Tag name for Log message
        private const val TAG = "Central"
        private val UUID_GAP = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")
    }

    // ble adapter
    private val bleAdapter: BluetoothAdapter = (SolluzApplication.context()
            .getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    override fun readData(addressType: DataReader.AddressType): String {
        if (!checkAvailable()) return ""

        val filters: MutableList<ScanFilter> = ArrayList()
        val scanFilter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID_GAP))
                .build()
        filters.add(scanFilter)

        // set low power scan mode

        // set low power scan mode
        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()

        bleAdapter?.bluetoothLeScanner.startScan(filters, settings, this)

        Toast.makeText(SolluzApplication.context(),
                "BLE is startScan", Toast.LENGTH_LONG).show()

        return ""
    }

    override fun onScanFailed(errorCode: Int) {
        Toast.makeText(SolluzApplication.context(),
                "BLE scan Failed1! $errorCode", Toast.LENGTH_LONG).show()
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        Toast.makeText(SolluzApplication.context(),
                "BLE scan Result $callbackType, $result", Toast.LENGTH_LONG).show()
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        Toast.makeText(SolluzApplication.context(),
                "BLE scan batch scan Result $, $results", Toast.LENGTH_LONG).show()
    }

    override fun updateSetting(address: String, code: String) {

    }


    private fun checkAvailable(): Boolean {
        // check ble adapter and ble enabled
        if (bleAdapter?.isEnabled != true) {
            Toast.makeText(SolluzApplication.context(),
                    "BLE is not enabled yet", Toast.LENGTH_LONG).show()
            requestEnableBLE();
            return false;
        }
        // check if location permission
        if (SolluzApplication.context()
                        .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(SolluzApplication.context(),
                    "Theis no permission for ACCESS_FINE_LOCATION", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun requestEnableBLE() {
        val bleEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        SolluzApplication.context().startActivity(bleEnableIntent);
    }
}