package com.solluzfa.solluzviewer.model

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.concurrent.TimeUnit


class MachineData(var name: MutableLiveData<String>, var state: MutableLiveData<Boolean>,
                  var passed: MutableLiveData<Int>, var failed: MutableLiveData<Int>,
                  var total: MutableLiveData<Int>, var passPercentage: MutableLiveData<Float>) {
    private val TAG = "MachineData"
    private val RETRY_COUNT = 10L

    lateinit var disposable: Disposable

    fun showState() {
        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    Observable.fromIterable(File("http://solluz.iptime.org/Data/MachineData1.txt").useLines { it.toList() })
                            .take(1)
                }
                .retry(RETRY_COUNT)
                .onErrorReturn { e ->
                    Log.e(TAG, e.toString())
                    "N:File error,S:N,G:1,N:1"
                }
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::update)
    }

    private fun update(packet: String) {
        //N:Solluz 비전 검사 장비,S:Y,G:100,N:2
        val datas = packet.split(",")
        val passedValue = datas[2].substringAfterLast(":").toInt()
        val failedValue = datas[3].substringAfterLast(":").toInt()
        name.value = datas[0].substringAfterLast(":")
        state.value = datas[1].substringAfterLast(":").equals("Y")
        passed.value = passedValue
        failed.value = failedValue
        total.value = passedValue + failedValue
        passPercentage.value = passedValue.toFloat() * 100 / (passedValue + failedValue)
    }

    fun clear() {
        if (::disposable.isInitialized) {
            if (!disposable.isDisposed()) disposable.dispose()
        }
    }
}
