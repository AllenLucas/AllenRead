package com.allenlucas.read.base.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

open class BaseViewHolder<T : ViewBinding> private constructor(val mBinding: T) :
    RecyclerView.ViewHolder(mBinding.root) {

    constructor(
        parent: ViewGroup,
        creator: (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> T
    ) : this(
        creator.invoke(
            LayoutInflater.from(parent.context), parent, false
        )
    )
}