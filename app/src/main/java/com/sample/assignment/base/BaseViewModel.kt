package com.sample.assignment.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.core.utility.event.Event
import com.sample.core.utility.extensions.empty
import com.sample.core.utility.manager.SessionManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import javax.inject.Inject


abstract class BaseViewModel : ViewModel() {

    @Inject
    lateinit var mSessionManager: SessionManager

    private var _showLoading =
        MutableLiveData<Event<Pair<Boolean, String>>>()
    val showLoadingLiveData: LiveData<Event<Pair<Boolean, String>>>
        get() = _showLoading

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {

        with(compositeDisposable) {
            if (!isDisposed) {
                dispose()
                compositeDisposable = CompositeDisposable()
            }
        }
        super.onCleared()
    }

    fun addDisposable(disposable: Disposable?) {

        disposable?.let {
            if (it.isDisposed.not()) {
                compositeDisposable.add(disposable)
            }
        }

    }

    fun subscribeSessionObserver(consumer: Consumer<String>) =
        mSessionManager.subscribeSession(consumer)

    fun clearSession() = mSessionManager.clearSession()

    fun showLoading(loadingMessage: String) {
        _showLoading.postValue(Event(Pair(true, loadingMessage)))
    }

    fun hideLoading() {
        _showLoading.postValue(Event(Pair(false, String.empty)))

    }


}