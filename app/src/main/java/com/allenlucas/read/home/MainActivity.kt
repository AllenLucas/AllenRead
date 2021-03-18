package com.allenlucas.read.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.allenlucas.read.base.ui.BaseActivity
import com.allenlucas.read.databinding.ActivityMainBinding

/**
 * Create by AllenLucas on 2021/03/14
 * 首页面
 */
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mViewModel by lazy { getViewModel(HomeViewModel::class.java) }
    private val mAdapter by lazy { NewAdapter() }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    override fun getViewBinding(): (inflater: LayoutInflater) -> ActivityMainBinding =
        ActivityMainBinding::inflate

    override fun init(savedInstanceState: Bundle?) {
        initRecyclerView()
        setSupportActionBar(binding.tool)
        baseObserve(mViewModel.dataLiveData) {
            if (0 == mViewModel.currentPage) {
                binding.swipeLayout.isRefreshing = false
                mAdapter.setData(it)
            } else {
                mAdapter.addData(it)
            }
        }
        mViewModel.getHomeNewList()
    }

    /**
     * 初始化recyclerView
     */
    private fun initRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mAdapter
        }
        binding.swipeLayout.setOnRefreshListener {
            mViewModel.currentPage = 0
            mViewModel.getHomeNewList()
        }
    }
}