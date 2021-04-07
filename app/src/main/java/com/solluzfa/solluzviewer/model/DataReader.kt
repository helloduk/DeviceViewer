package com.solluzfa.solluzviewer.model

interface DataReader {
    enum class AddressType {
        DATA, LAYOUT, PUSH
    }
    fun readData(addressType: AddressType): String
    fun updateSetting(address: String, code: String)
}