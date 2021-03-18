package com.allenlucas.read.web

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.allenlucas.read.base.model.BaseViewModel

/**
 * Create by AllenLucas on 2021/03/16
 */
class WebViewModel : BaseViewModel() {

    private val titleLiveData by lazy { MutableLiveData<String>() }
    private val progressLiveData by lazy { MutableLiveData<Int>() }
    private val chooserFileLiveData by lazy { MutableLiveData<Intent>() }
    private val startAppLiveData by lazy { MutableLiveData<Intent>() }
    private val imageOperaLiveData by lazy { MutableLiveData<String>() }
    private var uploadMessage: ValueCallback<Array<Uri>>? = null
    private var isDownload = true

    // 生命周期的回调
    private val lifecycleCallback: (Lifecycle.Event) -> Unit = {
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
            }
            Lifecycle.Event.ON_PAUSE -> {
            }
            Lifecycle.Event.ON_DESTROY -> {
            }
        }
    }

    // 长按逻辑处理
    private val longClick: (web: WebView) -> Boolean = {
        val hitTestResult = it.hitTestResult
        when (hitTestResult.type) {
            WebView.HitTestResult.IMAGE_TYPE, WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                imageOperaLiveData.value = hitTestResult.extra
                true
            }
            else -> false
        }
    }

    // webChromeClient
    private val mWebChromeClient by lazy {
        object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                titleLiveData.value = title
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressLiveData.value = newProgress
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                chooserFileLiveData.value =
                    Intent.createChooser(fileChooserParams?.createIntent(), "") ?: return false
                uploadMessage = filePathCallback
                return true
            }
        }
    }

    // webViewClient
    private val mWebViewClient by lazy {
        object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressLiveData.value = -1
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressLiveData.value = 200
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val uri = request?.url ?: return false
                val url = uri.toString()
                // 跳转其他应用的处理
                if (!url.startsWith("http")) {
                    startAppLiveData.value =
                        Intent.createChooser(Intent(Intent.ACTION_VIEW, uri).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }, "")
                    isDownload = false
                    return true
                }
                return false
            }

            //认证证书不被接受的情况下，重写该方法接受所有证书
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
//                super.onReceivedSslError(view, handler, error) 注释掉该行，设置有效
//                handler?.cancel()   Android默认的处理方式
                handler?.proceed()    //接受所有网站证书
            }
        }
    }

    /**
     * 下载监听
     */
    private val mDownloadListener by lazy {
        DownloadListener { url, _, _, _, _ ->
            if (isDownload) {
                startAppLiveData.value = Intent(Intent.ACTION_VIEW).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                    data = Uri.parse(url)
                }
            }
            isDownload = true
        }
    }

    /**
     * 初始化webView
     */
    fun initWebView(webView: WebView) {
        webView.apply {
            settings.apply {
                //默认不允许加载http与https混合内容,设置为允许
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                //自动加载图片，设置为false，所有图片都不加载
                loadsImagesAutomatically = false
                //开启js
                javaScriptEnabled = true
                //允许js弹窗
                javaScriptCanOpenWindowsAutomatically = true
                //设置可以访问文件
                allowFileAccess = true
                // 开启localStorage
                domStorageEnabled = true
                // 设置自适应屏幕
                useWideViewPort = true
                // 缩放至屏幕大小
                loadWithOverviewMode = true
            }
            webChromeClient = mWebChromeClient
            webViewClient = mWebViewClient
            setDownloadListener(mDownloadListener)
            setOnLongClickListener { return@setOnLongClickListener longClick.invoke(this) }
        }
    }

    /**
     * 上传文件
     */
    fun resultFile(request: Int, data: Intent?) {
        uploadMessage?.onReceiveValue(
            WebChromeClient.FileChooserParams.parseResult(request, data)
        )
        uploadMessage = null
    }

    /**
     * 分享链接
     */
    fun share(url: String, title: String) {
        startAppLiveData.value = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "${titleLiveData.value}\n${url}")
            type = "text/plain"
        }, title)
    }

    /**
     * 获取标题LiveData
     */
    fun titleObserve(): LiveData<String> = titleLiveData

    /**
     * 获取进度 LiveData
     */
    fun progressObserve(): LiveData<Int> = progressLiveData

    /**
     * 获取跳转 文件管理器 LiveData
     */
    fun chooserObserve(): LiveData<Intent> = chooserFileLiveData

    /**
     * 获取跳转App LiveData
     */
    fun startAppObserve(): LiveData<Intent> = startAppLiveData

    /**
     * 获取 图片处理 LiveData
     */
    fun imageOperaObserve(): LiveData<String> = imageOperaLiveData

    /**
     * 生命周期回调
     */
    fun getLifecycle() = lifecycleCallback
}