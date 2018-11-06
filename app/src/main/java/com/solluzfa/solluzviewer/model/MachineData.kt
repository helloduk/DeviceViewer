package com.solluzfa.solluzviewer.model

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


class MachineData(var name: MutableLiveData<String>, var state: MutableLiveData<Boolean>,
                  var passed: MutableLiveData<Int>, var failed: MutableLiveData<Int>,
                  var total: MutableLiveData<Int>, var passPercentage: MutableLiveData<Float>) {
    private val TAG = "MachineData"
    private val ERROR_PACKET = "N:File error,S:N,G:1,N:1"
    private val UrlAddress = URL("http://solluz.iptime.org/Data/MachineData1.txt");
    //private val UrlAddress = URL("https://helloduk.github.io/MachineData1.txt");
    lateinit var disposable: Disposable

    var dataObservable : Observable<String>

    init {
        dataObservable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    //Original file is encoded by "euc-kr"
                    Observable.just(UrlAddress.readText(Charset.forName("euc-kr")))
                    //Observable.just(readData())
                }
                .doOnError {e -> run {
                    Log.e(TAG, e.toString())
                    Observable.just(ERROR_PACKET)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::update)
                }}
                .retry()
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    // for reference. another method to get text.
    private fun readData() : String {
        //Original file is encoded by "euc-kr"
        val reader = BufferedReader(InputStreamReader(UrlAddress.openStream(), "euc-kr"))
        return reader.readLine()
    }

    fun showState() {
        Log.i(TAG, "showState")
        disposable = dataObservable.subscribe(this::update)
    }

    private fun update(packet: String) {
        Log.i(TAG, "update : " + packet)
        //N:Solluz 비전 검사 장비,S:Y,G:100,N:2
        val datas = packet.split(",")
        if(datas.size >= 4) {
            try {
                val passedValue = datas[2].substringAfterLast(":").toInt()
                val failedValue = datas[3].substringAfterLast(":").toInt()
                name.value = datas[0].substringAfterLast(":")
                state.value = datas[1].substringAfterLast(":").equals("Y")
                passed.value = passedValue
                failed.value = failedValue
                total.value = passedValue + failedValue
                passPercentage.value = passedValue.toFloat() * 100 / (passedValue + failedValue)
            } catch(e : Exception) {
                Log.e(TAG, "Invalid type ${e.toString()}");
            }
        } else {
            Log.e(TAG, "Invalid type");
        }
    }

    fun clear() {
        if (::disposable.isInitialized) {
            if (!disposable.isDisposed()) disposable.dispose()
        }
    }
}
