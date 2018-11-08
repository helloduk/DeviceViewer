package com.solluzfa.solluzviewer.model

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


object MachineData {
    private val TAG = "MachineData"

    //Example
    //Row:4,TT1:Text1,TB1:255255255,TF1:000000000,TT2:Text2,TB2:243175175,TF2:000000000,TT3:Text3,TB3:255255255,TF3:000000000,TT4:Text4,TB4:255255255,TF4:000000000
    //Name:Title Text,Row:4,CT1:Caption1,CB1:011097019,CF1:255255255,TA1:Right,CT2:Caption2,CB2:164020020,CF2:255255255,TA2:Left,CT3:Caption3,CB3:052118232,CF3:000000000,TA3:Center,CT4:Caption4,CB4:023108097,CF4:255255255,TA4:Center
    private val DATA_ERROR_PACKET = "Row:1,TT1:Error,TB1:255000000,TF1:000000255"
    private val LAYOUT_ERROR_PACKET = "Name:File Read Error,Row:1,CT1:Error,CB1:050050050,CF1:000000000,TA1:Right"
    private val ERROR_PACKET = "$DATA_ERROR_PACKET $LAYOUT_ERROR_PACKET"

    private var DataAddress = URL("http://solluz.iptime.org/Data/MachineData2.txt");
    private var LayoutAddress = URL("http://solluz.iptime.org/Data/MachineData2_Layout.txt");
    private var PushAddress = URL("http://solluz.iptime.org/Data/MachineData2_Push.txt");

    //private val UrlAddress = URL("https://helloduk.github.io/MachineData1.txt");
    lateinit var disposable: Disposable
    lateinit var subscriber : (String) -> Unit
    private var urlAddress : String? = null
    private var companyCode : String? = null
    private var interval : Long = 1000
    private var pushOn : Boolean = true

    val dObservable = Observable.defer{getDataObservable()}
    val lObservale = Observable.defer{getLayoutObservable()}
    val pObservale = Observable.defer{getPushObservable()}

    private fun getDataObservable() : Observable<String> {
        return Observable.interval(interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    //Original file is encoded by "euc-kr"
                    //Observable.just(DataAddress.readText(Charset.forName("euc-kr")))
                    Observable.just(readData(DataAddress))
                }
                .doOnError {e -> run {
                    Log.e(TAG, e.toString())
                    Observable.just(ERROR_PACKET)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(subscriber)
                }}
                .retry()
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getLayoutObservable() : Observable<String> {
        return Observable.interval(interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    //Original file is encoded by "euc-kr"
                    //Observable.just(LayoutAddress.readText(Charset.forName("euc-kr")))
                    Observable.just(readData(LayoutAddress))
                }
                .doOnError {e -> run {
                    Log.e(TAG, e.toString())
                    Observable.just(ERROR_PACKET)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(subscriber)
                }}
                .retry()
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getPushObservable() : Observable<String> {
        return Observable.interval(interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    //Original file is encoded by "euc-kr"
                    //Observable.just(PushAddress.readText(Charset.forName("euc-kr")))
                    Observable.just(readData(PushAddress))
                }
                .doOnError {e -> run {
                    Log.e(TAG, e.toString())
                    Observable.just(ERROR_PACKET)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(subscriber)
                }}
                .retry()
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    // for reference. another method to get text.
    private fun readData(source : URL) : String {
        //Original file is encoded by "euc-kr"
        val reader = BufferedReader(InputStreamReader(source.openStream(), "euc-kr"))
        return reader.readLine()
    }

    fun showState(f : (String) -> Unit) {
        Log.i(TAG, "showState")
        subscriber = f
        // SAM(Single Abstract Method) ambiguity issue.
        // so use io.reactivex.rxkotlin.Observables
        // or zip(.., .., BiFunction<String, String,String>{.., .. -> ..})
        disposable = Observables.zip(dObservable, lObservale){data, layout -> "$data $layout"}
                .subscribe(subscriber)
    }

//    private fun update(packet: String) {
//        Log.i(TAG, "update : " + packet)
//        //N:Solluz 비전 검사 장비,S:Y,G:100,N:2
//        val datas = packet.split(",")
//        if(datas.size >= 4) {
//            try {
//                val passedValue = datas[2].substringAfterLast(":").toInt()
//                val failedValue = datas[3].substringAfterLast(":").toInt()
//                name.value = datas[0].substringAfterLast(":")
//                state.value = datas[1].substringAfterLast(":").equals("Y")
//                passed.value = passedValue
//                failed.value = failedValue
//                total.value = passedValue + failedValue
//                passPercentage.value = passedValue.toFloat() * 100 / (passedValue + failedValue)
//            } catch(e : Exception) {
//                Log.e(TAG, "Invalid type ${e.toString()}");
//            }
//        } else {
//            Log.e(TAG, "Invalid type");
//        }
//    }

    fun updateSetting(address : String, code : String, time : Long, push : Boolean) {
        urlAddress = address
        companyCode = code
        DataAddress = URL("http://" + (if (address.last().equals("/")) address else "$address + /") + code + ".txt")
        LayoutAddress = URL("http://" + (if (address.last().equals("/")) address else "$address + /") + code + "_Layout.txt")
        PushAddress = URL("http://" + (if (address.last().equals("/")) address else "$address + /") + code + "_Push.txt")
        interval = time
        pushOn = push
    }

    fun clear() {
        if (::disposable.isInitialized) {
            if (!disposable.isDisposed()) disposable.dispose()
        }
        if (::subscriber.isInitialized) {
            subscriber = {}
        }
    }
}
