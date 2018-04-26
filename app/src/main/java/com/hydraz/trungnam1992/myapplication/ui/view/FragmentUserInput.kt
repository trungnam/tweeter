package com.hydraz.trungnam1992.myapplication.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hydraz.trungnam1992.myapplication.App
import com.hydraz.trungnam1992.myapplication.R
import com.hydraz.trungnam1992.myapplication.ui.contact.FragmentUserInputContact
import com.hydraz.trungnam1992.myapplication.ui.presenter.FragmentUserInputPresenter
import com.hydraz.trungnam1992.myapplication.ui.view.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_fragment_user_input.*
import javax.inject.Inject

class FragmentUserInput : BaseFragment(), FragmentUserInputContact.FragmentUserInputView {

    @Inject
    lateinit var mPresenter : FragmentUserInputPresenter

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) : FragmentUserInput{
            return FragmentUserInput()
        }
    }

    override fun initializePresenter() {
        mPresenter.attachView(this)
    }

    override fun initializeDagger() {
        App.appComponent.inject(this@FragmentUserInput)
    }

    override fun summitStatus() {

    }

    override fun changeFragment() {

    }

    override fun inputMessage() {

    }

    override fun showError(err: String) {
        textInputEditTextStatus.error = err
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater?.inflate(R.layout.fragment_fragment_user_input, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textInputEditTextStatus.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) = Unit

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mPresenter.checkInputMessage(strMsg = p0.toString())
            }

        })

    }
}
