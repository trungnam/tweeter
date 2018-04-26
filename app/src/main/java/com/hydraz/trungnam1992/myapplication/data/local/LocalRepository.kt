package com.hydraz.trungnam1992.myapplication.data.local

import android.content.Context
import android.util.Log
import com.hydraz.trungnam1992.myapplication.model.Status
import io.reactivex.Single
import io.realm.Realm
import javax.inject.Inject
import android.widget.Toast
import io.reactivex.Single.create
import io.reactivex.SingleEmitter
import io.realm.RealmChangeListener
import io.realm.exceptions.RealmException


/**
 * Created by trungnam1992 on 4/24/18.
 */
class LocalRepository @Inject constructor(realm: Realm) : LocalResource {


    val mRealm: Realm = realm
    override fun getStatus(): Single<ArrayList<Status>> {

        return create{
            e: SingleEmitter<ArrayList<Status>> ->

            val arr : ArrayList<Status> = ArrayList()

          mRealm.where(Status::class.java).findAll().apply {
              this.forEach { st : Status -> arr.add(st) }
          }
            when {
                arr.isEmpty() -> e.onError(throw Throwable("No status found!"))
                else -> {
                    e.onSuccess(arr)
                }
            }

        }

    }
    override fun saveStatus(array: ArrayList<String>, context: Context) {
        Log.e("LocalRepository" , "" + array)

        mRealm.executeTransaction({ realm ->
            try {
                for(stringsts : String in array){
                    val statusRealm = mRealm.createObject<Status>(Status::class.java)
                    statusRealm.statusMsg = stringsts
                    realm.copyToRealm(statusRealm)
                }
                Toast.makeText(context, "Success: ", Toast.LENGTH_SHORT).show()

            } catch (e: RealmException) {
                Toast.makeText(context, "Fail: ", Toast.LENGTH_SHORT).show()

            }
        })
    }

}