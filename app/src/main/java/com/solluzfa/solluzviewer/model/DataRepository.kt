package com.solluzfa.solluzviewer.model

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class DataRepository private constructor() {
    companion object {
        private val TAG = "MachineData"

        @Volatile
        private var instance: DataRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            DataRepository().also { instance = it }
        }
    }

    //Example
    //Row:4,TT1:Text1,TB1:255255255,TF1:000000000,TT2:Text2,TB2:243175175,TF2:000000000,TT3:Text3,TB3:255255255,TF3:000000000,TT4:Text4,TB4:255255255,TF4:000000000
    //Name:Title Text,Row:4,CT1:Caption1,CB1:011097019,CF1:255255255,TA1:Right,CT2:Caption2,CB2:164020020,CF2:255255255,TA2:Left,CT3:Caption3,CB3:052118232,CF3:000000000,TA3:Center,CT4:Caption4,CB4:023108097,CF4:255255255,TA4:Center
    private val DATA_ERROR_PACKET = "Row:1,TT1:Error,TB1:255000000,TF1:000000255"
    private val LAYOUT_ERROR_PACKET = "Name:File Read Error,Row:1,CT1:Error,CB1:050050050,CF1:000000000,TA1:Right"
    private val ERROR_PACKET = "$DATA_ERROR_PACKET;$LAYOUT_ERROR_PACKET"
    private val PUSH_ERROR_PACKET = "Time:20180101000000,Data:Push Error"

    //private val UrlAddress = URL("https://helloduk.github.io/MachineData1.txt");
    lateinit var dataDisposable: Disposable
    lateinit var pushDisposable: Disposable

    lateinit var dataSubscriber: (String) -> Unit
    lateinit var pushSubscriber: (String) -> Unit

    private var interval: Long = 1000
    private var pushOn: Boolean = true

    private val dObservable = Observable.defer { getDataObservable() }
    private val lObservale = Observable.defer { getLayoutObservable() }
    private val pObservale = Observable.defer { getPushObservable() }

    private var dataReader: DataReader = DataReaderURL()
//    private var dataReader = DataReaderBluetooth()

    private fun getDataObservable(): Observable<String> {
        return Observable.interval(0L, interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    //Original file is encoded by "euc-kr"
                    //Observable.just(DataAddress.readText(Charset.forName("euc-kr")))
                    Observable.just(dataReader.readData(DataReader.AddressType.DATA))
                }
                .doOnError { e ->
                    run {
                        e.printStackTrace()
                        Observable.just(ERROR_PACKET)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(dataSubscriber)
                    }
                }
                .retry()
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getLayoutObservable(): Observable<String> {
        return Observable.interval(0L, interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    //Original file is encoded by "euc-kr"
                    //Observable.just(LayoutAddress.readText(Charset.forName("euc-kr")))
                    Observable.just(dataReader.readData(DataReader.AddressType.LAYOUT))
                }
                .doOnError { e ->
                    run {
                        e.printStackTrace()
                        Observable.just(ERROR_PACKET)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(dataSubscriber)
                    }
                }
                .retry()
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getPushObservable(): Observable<String> {
        return Observable.interval(0L, interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { _ ->
                    //Original file is encoded by "euc-kr"
                    //Observable.just(PushAddress.readText(Charset.forName("euc-kr")))
                    Observable.just(dataReader.readData(DataReader.AddressType.PUSH))
                }
                .doOnError { e ->
                    run {
                        e.printStackTrace()
                        Observable.just(PUSH_ERROR_PACKET)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(pushSubscriber)
                    }
                }
                .retry()
                .doOnNext { data -> Log.i(TAG, data) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun showState(ds: (String) -> Unit, ps: (String) -> Unit) {
        Log.i(TAG, "showState")
        clear()
        if (interval == 0L) {
            Log.i(TAG, "Do not update. Interval is None")
            return
        }

        dataSubscriber = ds
        pushSubscriber = ps
        // SAM(Single Abstract Method) ambiguity issue.
        // so use io.reactivex.rxkotlin.Observables
        // or zip(.., .., BiFunction<String, String,String>{.., .. -> ..})
        dataDisposable = Observables.zip(dObservable, lObservale) { data, layout -> "$data;$layout" }
                .subscribe(dataSubscriber) {
                    Log.e(TAG, it.message)
                }
        if (pushOn)
            pushDisposable = pObservale.subscribe(pushSubscriber) {
                Log.e(TAG, it.message)
            }
    }

    fun updateSetting(bluetooth: Boolean, address: String, code: String, time: Long, push: Boolean) {
        dataReader = if (bluetooth)
            DataReaderBluetooth()
        else
            DataReaderURL()
        dataReader.updateSetting(address, code)

        interval = time
        pushOn = push
        Log.i(TAG, "setting updated : $interval $pushOn")
    }

    fun clear() {
        Log.i(TAG, "clear")
        if (::dataDisposable.isInitialized) {
            if (!dataDisposable.isDisposed()) dataDisposable.dispose()
        }
        if (::pushDisposable.isInitialized) {
            if (!pushDisposable.isDisposed()) pushDisposable.dispose()
        }

        if (::dataSubscriber.isInitialized) {
            dataSubscriber = {}
        }

        if (::pushSubscriber.isInitialized) {
            pushSubscriber = {}
        }
    }
}
