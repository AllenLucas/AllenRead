package com.allenlucas.read.home

import android.text.TextUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.allenlucas.read.web.WebViewActivity
import com.allenlucas.read.base.api.ApiServiceManager
import com.allenlucas.read.base.api.DataBean
import com.allenlucas.read.base.ui.BaseViewHolder
import com.allenlucas.read.databinding.ItemNewLayoutBinding
import com.allenlucas.read.getViewHolder
import com.allenlucas.read.onSingleClick
import com.allenlucas.read.utils.ImageUtils

class NewAdapter : RecyclerView.Adapter<BaseViewHolder<ItemNewLayoutBinding>>() {

    private val dataList = mutableListOf<DataBean>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemNewLayoutBinding> {
        return parent.getViewHolder(ItemNewLayoutBinding::inflate)
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: BaseViewHolder<ItemNewLayoutBinding>, position: Int) {
        ImageUtils.instance.imageLoad(fullUrl(getImage(position)), holder.mBinding.ivCover)
        holder.mBinding.tvTitle.text = dataList[position].frontmatter?.title ?: ""
        holder.itemView.onSingleClick({
            WebViewActivity.start(it.context, fullUrl(webPath(position)))
        })
    }

    //获取标题
    private fun getTitle(position: Int) = dataList[position].frontmatter?.title ?: ""

    private fun webPath(position: Int) = dataList[position].frontmatter?.path ?: ""

    //获取返回的图片路径
    private fun getImage(position: Int) =
        dataList[position].frontmatter?.banner?.childImageSharp?.fixed?.src ?: ""

    /**
     * 拼接链接的全路径
     */
    private fun fullUrl(url: String) = when {
        TextUtils.isEmpty(url) -> ""
        url.startsWith("http") -> url
        url.startsWith("/") -> "${ApiServiceManager.BASE_URL}$url"
        else -> ""
    }

    /**
     * 刷新数据
     */
    fun setData(data: List<DataBean>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    /**
     * 添加数据
     */
    fun addData(data: List<DataBean>) {
        dataList.addAll(data)
        notifyItemRangeInserted(dataList.size - data.size, data.size)
    }

}