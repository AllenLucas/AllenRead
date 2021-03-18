package com.allenlucas.read.web

import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * webView的生命周期调用
 */
class WebLifecycle(webView: WebView) : LifecycleObserver {

    private var mWebView: WebView? = webView
    private var lifecycleCallback: ((Lifecycle.Event) -> Unit)? = null

    fun setLifecycleListener(lifecycleCallback: (Lifecycle.Event) -> Unit) {
        this.lifecycleCallback = lifecycleCallback
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun webResume() {
        lifecycleCallback?.invoke(Lifecycle.Event.ON_RESUME)
        mWebView?.apply {
            onResume()
            resumeTimers()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun webPause() {
        lifecycleCallback?.invoke(Lifecycle.Event.ON_PAUSE)
        mWebView?.apply {
            onPause()
            pauseTimers()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        lifecycleCallback?.invoke(Lifecycle.Event.ON_DESTROY)
        mWebView?.apply {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            destroy()
        }
        lifecycleCallback = null
        mWebView = null
    }
}