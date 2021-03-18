package com.allenlucas.read.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.allenlucas.read.databinding.DialogImageBinding
import com.allenlucas.read.onSingleClick
import com.allenlucas.read.utils.ImageUtils

/**
 * Create by AllenLucas on 2021/03/17
 * 查看图片的dialog
 */
class ImageDialog private constructor() : DialogFragment() {

    companion object {
        fun show(fragmentManager: FragmentManager, url: String) {
            ImageDialog().apply {
                arguments = Bundle().apply { putString("imageUrl", url) }
            }.show(fragmentManager, "ImageDialog")
        }
    }

    private lateinit var binding: DialogImageBinding
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments?.getString("imageUrl", "") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (TextUtils.isEmpty(url)) {
            dismiss()
            return
        }
        binding.ivImage.onSingleClick({ dismiss() })
        if (url.startsWith("http")) {
            ImageUtils.instance.imageLoadDefault(url, binding.ivImage)
            return
        }
        dismiss()
    }

    override fun onResume() {
        val params = dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }
        dialog?.window?.attributes = params
        super.onResume()
    }
}