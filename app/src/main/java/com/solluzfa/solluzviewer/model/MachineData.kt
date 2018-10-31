package com.solluzfa.solluzviewer.model

import io.reactivex.Observable
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

data class MachineData(var name:String, var state: Boolean, var passed : Int, var failed : Int, var passPercentage : Float)


fun MachineData.update(packet : String) {
    //Observable<ArrayList<String>>.

    val stream : List<String> = File("http://solluz.iptime.org/Data/MachineData1.txt").useLines { it.toList() }

    //N:Solluz 비전 검사 장비,S:Y,G:100,N:2
    val datas = packet.split(",")
    name = datas[0].substringAfterLast(":")
    state = datas[1].substringAfterLast(":").equals("Y")
    passed = datas[2].substringAfterLast(":").toInt()
    failed = datas[3].substringAfterLast(":").toInt()
    passPercentage = passed.toFloat() * 100 / (passed + failed)
}
