package com.allenlucas.read.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.allenlucas.read.R
import com.allenlucas.read.base.ui.BaseActivity
import com.allenlucas.read.databinding.ActivityWebviewBinding
import com.allenlucas.read.dialog.ImageDialog

/**
 * Create by AllenLucas on 2021/03/15
 * webView页面
 */
class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {

    companion object {
        const val FILE_CODE = 620
        fun start(context: Context, url: String) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra("url", url)
            })
        }
    }

    // viewModel
    private val mViewModel by lazy { getViewModel(WebViewModel::class.java) }

    //webView生命周期调用
    private val webLifecycle by lazy { WebLifecycle(binding.webView) }

    override fun getViewBinding(): (inflater: LayoutInflater) -> ActivityWebviewBinding =
        ActivityWebviewBinding::inflate

    override fun init(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.tool)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val url = intent.getStringExtra("url") ?: ""
        mViewModel.initWebView(binding.webView)
        initLifecycle()
        initObserve()
        binding.webView.loadUrl(url)
    }

    /**
     * 绑定lifecycle，及其他操作
     */
    private fun initLifecycle() {
        lifecycle.addObserver(webLifecycle)
        webLifecycle.setLifecycleListener(mViewModel.getLifecycle())
    }

    /**
     * liveData的观测
     */
    private fun initObserve() {
        baseObserve(mViewModel.titleObserve()) { supportActionBar?.title = it }
        baseObserve(mViewModel.progressObserve()) {
            when {
                it < 0 -> binding.progress.visibility = View.VISIBLE
                it > 100 -> {
                    binding.progress.visibility = View.GONE
                    if (!binding.webView.settings.loadsImagesAutomatically) {
                        binding.webView.settings.loadsImagesAutomatically = true
                    }
                }
                else -> binding.progress.progress = it
            }
        }
        baseObserve(mViewModel.chooserObserve()) { startActivityForResult(it, FILE_CODE) }
        baseObserve(mViewModel.startAppObserve()) { startActivity(it) }
        baseObserve(mViewModel.imageOperaObserve()) { ImageDialog.show(supportFragmentManager, it) }
    }

    // 返回
    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
            return
        }
        super.onBackPressed()
    }

    // toolbar 点击
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_share ->
                mViewModel.share(
                    binding.webView.url ?: return super.onOptionsItemSelected(item),
                    getString(R.string.share)
                )
        }
        return super.onOptionsItemSelected(item)
    }

    // 返回值
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK && requestCode != FILE_CODE) return
        mViewModel.resultFile(requestCode, data)
    }
}