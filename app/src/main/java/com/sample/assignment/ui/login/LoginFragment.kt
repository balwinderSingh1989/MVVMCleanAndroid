package com.sample.assignment.ui.login

import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sample.assignment.BR
import com.sample.assignment.R
import com.sample.assignment.base.BaseFragment
import com.sample.assignment.databinding.LoginFragmentBinding
import com.sample.core.utility.extensions.empty
import com.sample.core.utility.extensions.safeGet

class LoginFragment : BaseFragment<LoginFragmentBinding, LoginViewModel>() {

    override val viewModel = LoginViewModel::class.java


    override val bindingVariable = BR.viewModel

    override fun getLayoutId() = R.layout.login_fragment


    override fun initUserInterface(rootView: View) {

        observeLoginSuccessEvent()

        observeFailEvents()

        observeClickEvents()
        initFocusListners()

    }

    private fun observeClickEvents() {
        injectedViewModel.loginClicked.observe(viewLifecycleOwner, Observer {

            injectedViewModel.login(
                viewDataBinding.tvEmail.editText?.text.toString().safeGet(),
                viewDataBinding.tvPassword.editText?.text.toString().safeGet()
            )

        })
    }

    private fun initFocusListners() {
        viewDataBinding.tvEmail.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    injectedViewModel.errorEmail.set(String.empty)
            }

        viewDataBinding.tvPassword.editText?.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    injectedViewModel.errorPassword.set(String.empty)
            }
    }

    private fun observeFailEvents() {
        injectedViewModel.loginFailEvent.observe(viewLifecycleOwner, Observer {
            showFragmentDialog("Error", it)

        })
    }

    private fun observeLoginSuccessEvent() {
        injectedViewModel.loginSuccessEvent.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(R.id.action_login_fragment_to_profilr_fragment)
        })
    }

}
