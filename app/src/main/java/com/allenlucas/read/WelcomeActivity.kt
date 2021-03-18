package com.allenlucas.read

import android.os.Bundle
import android.view.LayoutInflater
import com.allenlucas.read.base.ui.BaseActivity
import com.allenlucas.read.databinding.ActivityWelcomeBinding
import com.allenlucas.read.home.MainActivity

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    override fun getViewBinding(): (inflater: LayoutInflater) -> ActivityWelcomeBinding =
        ActivityWelcomeBinding::inflate

    override fun init(savedInstanceState: Bundle?) {
        binding.tvWelcome.postDelayed({ MainActivity.start(this) }, 2000)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}