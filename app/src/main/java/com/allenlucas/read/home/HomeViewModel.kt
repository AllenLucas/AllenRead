package com.allenlucas.read.home

import androidx.lifecycle.MutableLiveData
import com.allenlucas.read.base.api.ApiServiceManager
import com.allenlucas.read.base.api.DataBean
import com.allenlucas.read.base.model.BaseViewModel

class HomeViewModel : BaseViewModel() {

    var currentPage = 0
    val dataLiveData by lazy { MutableLiveData<List<DataBean>>() }

    fun getHomeNewList() {
        requestData({ ApiServiceManager.api.getNewList() }, dataLiveData, { currentPage++ },
            {
                currentPage--
                if (currentPage < 0) currentPage = 0
            })
    }
}