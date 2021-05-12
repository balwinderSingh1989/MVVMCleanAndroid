package com.sample.assignment.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.sample.assignment.R
import com.sample.assignment.util.DialogUtils
import com.sample.assignment.util.dialogs.AlertDialogListener
import com.sample.assignment.util.extension.relaunchApp
import com.sample.assignment.util.extension.showView
import com.sample.core.utility.KeyBoardUtils
import com.sample.core.utility.extensions.TAG
import com.sample.core.utility.extensions.empty
import com.sample.core.utility.extensions.isConnected
import com.sample.core.utility.logger.AppLogger
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.Disposable
import javax.inject.Inject

abstract class BaseActivity<VDB : ViewDataBinding, BVM : BaseViewModel> :
    DaggerAppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var injectedViewModel: BVM

    private lateinit var viewDataBinding: VDB

    abstract val viewModel: Class<BVM>

    abstract fun getBindingVariable(): Int

    @get:LayoutRes
    abstract val layoutId: Int

    private var sessionDisposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        initUserInterface()

    }

    override fun onBackPressed() {
        currentFocus?.let {
            KeyBoardUtils.hideKeyboard(it)
        }
        super.onBackPressed()
    }

    protected abstract fun initUserInterface()


    private fun performDataBinding() {

        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)

        (viewDataBinding.root as ViewGroup).addView(
            layoutInflater.inflate(
                R.layout.activity_base,
                null
            )
        )

        injectedViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(viewModel)

        viewDataBinding.setVariable(getBindingVariable(), injectedViewModel)

        viewDataBinding.executePendingBindings()

    }


    fun toggleLoadingDialog(show: Boolean, message: String = String.empty) {
        if (show) {
            showLoading(message)
        } else {
            hideLoading()
        }
    }

    private fun showLoading(message: String) {
        hideLoading()
        viewDataBinding.root.findViewById<ConstraintLayout>(R.id.progress).showView(true)
    }


    private fun hideLoading() {
        viewDataBinding.root.findViewById<ConstraintLayout>(R.id.progress).showView(false)
    }

    fun isConnected() = this.applicationContext.isConnected()


    /**
     * Subscribe session manager observers for callbacks
     */
    private fun subscribeSession() {
        AppLogger.d(TAG, "subscribeSession()")
        sessionDisposable =
            injectedViewModel.subscribeSessionObserver { onSessionExpired() }

    }

    /**
     * Unsubscribe session manager observers from callbacks
     */
    private fun unsubscribeSession() {
        AppLogger.d(TAG, "unsubscribeSession()")
        sessionDisposable?.dispose()
        sessionDisposable = null
    }

    /**
     * Show session expired dialog with masked background..
     * its a good practice to mask a UI with splash screen
     */
    private fun onSessionExpired() {
        if (isFinishing.not()) {
            AppLogger.d(TAG, "onSessionExpired()")
            maskAppUi()
            hideLoading()
            showSessionTimeoutDialog()
        }
    }


    /**
     * Mask/unmask app UI with the overlay
     */
    fun maskAppUi(mask: Boolean = true) {
        if (mask) {
            hideKeyboard()
        }
        viewDataBinding.root.bringToFront()
        viewDataBinding.root.findViewById<ConstraintLayout>(R.id.layoutMask).showView(mask)
    }

    fun hideKeyboard() {
        this.currentFocus?.let {
            KeyBoardUtils.hideKeyboard(it)
        }
    }


    /**
     * Show session timeout dialog with an option to show custom dialog button text
     */
    private fun showSessionTimeoutDialog(
        dialogButtonText: String = getString(
            R.string.login
        ),
        title: String = getString(
            R.string.error_session_title

        ),
        message: String = getString(
            R.string.error_session_timeout_message

        )
    ) {
        DialogUtils
            .showInfoDialog(
                context = this,
                title = title,
                message = message,
                buttonName = dialogButtonText,
                cancelable = false,
                alertDialogListener = object : AlertDialogListener {
                    override fun onPositive() {
                        AppLogger.d(TAG, "onSessionExpired().onPositive()")
                        relaunchApp()
                    }

                    override fun onNegative() {
                        TODO("Not yet implemented")
                    }
                }
            )
    }

    override fun onStart() {
        super.onStart()
        subscribeSession()
    }

    override fun onStop() {
        unsubscribeSession()
        super.onStop()
    }


}