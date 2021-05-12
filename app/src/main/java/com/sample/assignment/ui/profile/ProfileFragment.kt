package com.sample.assignment.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sample.assignment.BR
import com.sample.assignment.R
import com.sample.assignment.base.BaseFragment
import com.sample.assignment.databinding.ProfileFragmentBinding
import com.sample.assignment.util.DialogUtils
import com.sample.assignment.util.FileUtils
import com.sample.assignment.util.dialogs.AlertDialogListListener
import com.sample.assignment.util.dialogs.AlertDialogListener
import com.sample.assignment.util.extension.showPermissionFromSettingsDialog
import com.sample.assignment.util.permission.askPermission
import com.sample.core.domain.remote.exception.PermissionException
import com.sample.core.utility.event.EventObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class ProfileFragment : BaseFragment<ProfileFragmentBinding, ProfileViewModel>() {

    companion object {
        const val CAMERA = 0
        const val GALLERY = 1
        const val REQUEST_IMAGE_CAPTURE = 100
        const val REQUEST_IMAGE_SELECTION = 200
        const val EXTRA_MIME_TYPE = "*/*"
    }

    override val viewModel = ProfileViewModel::class.java

    override val bindingVariable = BR.viewModel

    override fun getLayoutId() = R.layout.profile_fragment


    override fun initUserInterface(rootView: View) {

        injectedViewModel.popualteUI()
        observeCamerClickEvent()
        observeLogoutEvent()
        observeGavatarLoadFailEvent()
    }

    private fun observeCamerClickEvent() {
        injectedViewModel.attachmentClickEvent.observe(viewLifecycleOwner, Observer {
            showImageSelectionOption()
        })
    }

    private fun observeGavatarLoadFailEvent() {
        injectedViewModel.gavatarAPIfailureEvent.observe(viewLifecycleOwner, EventObserver {
            showFragmentDialog("Error", it)
        })
    }

    private fun observeLogoutEvent() {
        injectedViewModel.logoutEvent.observe(viewLifecycleOwner, Observer {

            DialogUtils.twoButtonDialog(
                context = context,
                message = getString(R.string.logout_message),
                alertDialogListener = object : AlertDialogListener {
                    override fun onPositive() {
                        //try to pop back stack first..if false , set change destination
                        moveToLogin()
                    }

                    override fun onNegative() {}
                },
                positiveButtonText = getString(R.string.logout_positive),
                negativeButtonText = "No",
                cancelable = false
            )


        })
    }

    private fun moveToLogin() {
        findNavController().let {
            if (it.popBackStack().not()) {
                it.graph.apply {
                    startDestination = R.id.login_fragment
                }.run {
                    it.graph = this
                }
            }
        }
    }

    private fun showImageSelectionOption() {
        DialogUtils.showListDialog(
            context = requireContext(),
            optionsList = getAttachmentOptions(),
            alertDialogListListener = object :
                AlertDialogListListener {
                override fun onOptionSelected(selectedOption: Int) {
                    checkPermission(selectedOption)
                }
            }
        )
    }


    private fun getAttachmentOptions(): MutableList<String> {
        val attachmentOptions = mutableListOf<String>()
        //TODO this stings should be in string.xml
        attachmentOptions.add("Camera")
        attachmentOptions.add("Gallery")
        return attachmentOptions
    }


    fun checkPermission(attachmentType: Int) {
        lifecycleScope.launch {
            try {
                when (attachmentType) {
                    CAMERA -> {
                        askPermission(Manifest.permission.CAMERA)

                        initiateCameraImageCapture()
                    }
                    GALLERY -> {
                        askPermission(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        initiateImageSelection()
                    }

                }
            } catch (permissionException: PermissionException) {
                if (permissionException.hasForeverDenied()) {

                    activity?.showPermissionFromSettingsDialog(
                        getString(R.string.permission_general),
                        getString(R.string.settings),
                        permissionException

                    )
                }
            }
        }
    }


    private fun initiateCameraImageCapture() {

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            takePictureIntent.resolveActivity(requireActivity().packageManager)
                ?.also {
                    startActivityForResult(
                        takePictureIntent,
                        REQUEST_IMAGE_CAPTURE
                    )
                }
        }
    }

    private fun initiateImageSelection() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            injectedViewModel.allowedMimeType
        )
        intent.type = EXTRA_MIME_TYPE
        startActivityForAttachment(intent, REQUEST_IMAGE_SELECTION)
    }

    private fun startActivityForAttachment(intent: Intent, requestCode: Int) {
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    lateinit var file: File


                    lifecycleScope.launch(Dispatchers.IO) {
                        file = injectedViewModel.compressCameraImage(imageBitmap)
                        injectedViewModel.userImageFile.set(file)
                    }
                }
                REQUEST_IMAGE_SELECTION -> {
                    data?.let {
                        val uri = it.data
                        val imagePath = FileUtils.getPath(requireContext(), uri)
                        injectedViewModel.cacheGallerySelectedPhotoPath(imagePath)
                        injectedViewModel.userImageFile.set(File(imagePath))
                    }
                }
                else -> {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}