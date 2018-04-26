package com.hydraz.trungnam1992.myapplication.ui.presenter

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.hydraz.trungnam1992.myapplication.data.DataRepository
import com.hydraz.trungnam1992.myapplication.model.Status
import com.hydraz.trungnam1992.myapplication.ui.contact.FragmentUserInputContact
import com.hydraz.trungnam1992.myapplication.utils.StringUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by trungnam1992 on 4/25/18.
 */
class FragmentUserInputPresenter @Inject constructor(
            //todo
            private val dataRepository: DataRepository,
            private val compositeDisposable: CompositeDisposable
        ) : BasePresenter<FragmentUserInputContact.FragmentUserInputView>() , FragmentUserInputContact.Presenter {

    @Inject lateinit var mDataRepository: DataRepository

    private lateinit var mView : FragmentUserInputContact.FragmentUserInputView

    private var arrSave : ArrayList<String> = ArrayList()
    override fun detachView(view: FragmentUserInputContact.FragmentUserInputView) = Unit

    override fun attachView(view: FragmentUserInputContact.FragmentUserInputView) {
        mView = view

    }

    override fun checkInputMessage(strMsg: String) {

        val disposable = PublishSubject.just(strMsg)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map({
                    //check empty before split msg
                    if (it.isEmpty()) {
                        throw Exception("Empty Message")
                    }
                    return@map it
                })
                .flatMap(
                        {
                            return@flatMap splitMessageObserverble(it)
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { arr: ArrayList<String> ->
                            Log.e("nnam :", "" + arr)
                            when {
                                arr.isEmpty() -> throw Exception("Message cannot chunk")
                                else -> {
                                    mView.enableSummirButton(true)
                                    arrSave = arr
                                }
                            }
                            //to do save to DB
                        },
                        { err: Throwable ->
                            //show error
                            mView.showError(err.message.toString())
                            mView.enableSummirButton(false)

                        })

        compositeDisposable.add(disposable)

    }

    fun save(context: Context){
        dataRepository.saveListStatus(arrSave ,context)

//        dataRepository.getListStatus().delay(300, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    t ->
//                    Log.e("dddxxx" , "" + t)
//                }, {
//                    t ->
//                    Log.e("dddxxx" , "" + t.message)
//                })
    }
    override fun splitMessageObserverble(strMsg: String): Observable<ArrayList<String>> {

        return Observable.create {
            try {

                it.onNext(splitMessage(strMsg))

            }catch (e : Exception){

            }
        }
    }


    fun splitMessage(str: String): ArrayList<String> {

        val arrayList: ArrayList<String> = ArrayList()

        when {
            str.length < 50 ->
                return arrayListOf(str)
            str.length > 50 -> {
                //to do
                val strMsg = str
                val lenghtOfMsg = strMsg.length
                val size = strMsg.length / 50
                if (strMsg.length % 50 != 0) {
                    size + 1 // increase 1
                }
                val arrayIndicator = (0..size).mapTo(ArrayList()) { "${it +1 }" + "/" + "${size +1 } " }

                var finalStr = strMsg
                for (a: String in arrayIndicator) {
                    finalStr += a
                }
                //return arr
                var currentIndex = 0
                var idicatorLenght = 0
                var strTmp = ""
                (0..size).forEach { i ->
                    strTmp += arrayIndicator[i]
                    when (i) {
                        0 -> {
                            (0..(50 - arrayIndicator[i].length - 1)).forEach { j ->
                                strTmp += strMsg.toCharArray()[j]
                                currentIndex = j
                            }
                            idicatorLenght += arrayIndicator[i].length

                            if (!TextUtils.isEmpty(strMsg.toCharArray()[currentIndex].toString().trim())) {
                                //return null arr
                                return arrayList
                            }
                            arrayList.add(strTmp)
                        }
                        else -> {
                            strTmp = "" //clear
                            strTmp += arrayIndicator[i]
                            currentIndex + 1
                            idicatorLenght += arrayIndicator[i].length
                            when {
                                50 * (i + 1) > finalStr.length -> {
                                    for (k in currentIndex..(lenghtOfMsg - 1)) {
                                        strTmp += strMsg.toCharArray()[k]
                                    }
                                }
                                else -> {
                                    for (k in currentIndex..(50 * (i + 1) - (idicatorLenght) - 2)) {
                                        strTmp += strMsg.toCharArray()[k]
                                        currentIndex = k
                                    }
                                    if (!TextUtils.isEmpty(strMsg.toCharArray()[currentIndex].toString().trim())) {
                                        //return null arr
                                        arrayList.clear()
                                        return arrayList
                                    }
                                    idicatorLenght += arrayIndicator[i].length
                                }
                            }
                            //save to array
                            arrayList.add(strTmp)
                        }
                    }
                }
            }
        }
        return arrayList
    }
}