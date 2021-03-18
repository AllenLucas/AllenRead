## Coding  Test

### 网络框架 

- okHttp  +   Retrofit + 协程

- 对于无网络的处理

  - 使用okHttp的拦截器的方式，请求接口时，判断有没有网络，如果有网络正常请求，如果没有网络，强制使用缓存

  ```kotlin
  /**
   * 处理缓存拦截器
   */
  class RequestCacheInterceptor : Interceptor {
      override fun intercept(chain: Interceptor.Chain): Response {
          val request = chain.request()
          val requestBuilder = request.newBuilder()
          if (!NetWorkUtils.isNetWorkAvailable()) {
              requestBuilder.cacheControl(CacheControl.FORCE_CACHE)
          }
          return chain.proceed(requestBuilder.build())
      }
  }
  ```

  

  - 获取到数据后，头部添加缓存信息

  ```kotlin
  /**
   * 获取到数据后，头部添加缓存信息
   */
  class ResponseCacheInterceptor : Interceptor {
      override fun intercept(chain: Interceptor.Chain): Response {
          return chain.proceed(chain.request()).newBuilder()
              .removeHeader("Pragma")
              .removeHeader("Cache-Control")
              .addHeader("Cache-Control", CacheControl.FORCE_CACHE.toString())
              .build()
      }
  }
  ```

  

### 图片

- 使用Glide作为图片的加载库
  - 使用Glide的缓存模式，对图片进行缓存
  - [GlideV4默认解码格式为8888](https://muyangmin.github.io/glide-docs-cn/doc/migrating.html#%E8%A7%A3%E7%A0%81%E6%A0%BC%E5%BC%8F)，设置图片加载解码为RGB565，减少内存占用
  
  ```kotlin
  @GlideModule
  class ConfigModule : AppGlideModule() {
      override fun applyOptions(context: Context, builder: GlideBuilder) {
          builder.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_RGB_565))
      }
  }
  ```
  
  
  
  - 通过内存占用，对glide的缓存进行处理
  
  ```kotlin
      override fun onTrimMemory(level: Int) {
          super.onTrimMemory(level)
          //app 置换到后台 清空 glide缓存
          if (ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN == level){
              Glide.get(this).clearMemory()
          }
          //其他情况，glide自行处理内存
          Glide.get(this).trimMemory(level)
      }
  
      override fun onLowMemory() {
          super.onLowMemory()
          // 低内存 情况glide 缓存
          Glide.get(this).clearMemory()
      }
  ```
  
  



### WebView

- 使用系统的webView进行封装，
- 加载页面禁止加载图片，加载完成再加载图片，加快页面加载速度
- 防止证书问题加载失败

```kotlin
override fun onReceivedSslError(view: WebView?,handler: SslErrorHandler?,error: SslError?) {
//       super.onReceivedSslError(view, handler, error) 注释掉该行，设置有效
//       handler?.cancel()   Android默认的处理方式
		handler?.proceed()    //接受所有网站证书
}
```



- 与H5交互，选择文件的处理,隐式跳转

```kotlin
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
```



- 打开其他应用的处理

```kotlin
override fun shouldOverrideUrlLoading(view: WebView?,request: WebResourceRequest?): Boolean {
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
```



- 下载的处理，隐式跳转

```kotlin
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

...
webView.setDownloadListener(mDownloadListener)
...
```



- 分享链接的处理

```kotlin
/**
 * 分享链接
 */
fun share(url: String, title: String) {
        startAppLiveData.value = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, "${titleLiveData.value}\n${url}")
            type = "text/plain"
        }, title)
}
```



- 长按显示H5中图片的处理

```kotlin
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
```

