package com.timmy.mapmetropia.base

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlin.collections.ArrayList


abstract class BaseRecyclerViewDataBindingAdapter<T>(private val context: Context, private val layoutID:Int) :
     androidx.recyclerview.widget.RecyclerView.Adapter<BaseRecyclerViewDataBindingAdapter<T>.ViewHolder>(), View.OnClickListener, View.OnLongClickListener {
     val TAG = javaClass.simpleName
     private var list = mutableListOf<T>()

     private val myInflater: LayoutInflater? = null
     private var sortKey: String? = null

     /**
      * 初始化 ViewHolder
      * */
     abstract fun initViewHolder(viewHolder: ViewHolder)

     /**
      * 每次 ViewHolder 變化
      * */
     abstract fun onBindViewHolder(viewHolder: ViewHolder, position: Int , data:T)

     /**
      * 點擊事件
      * @return true 為以處理
      * */
     abstract fun onItemClick(view: View, position: Int, data: T) : Boolean

     /**
      * 長按事件
      * @return true 為以處理
      * */
     abstract fun onItemLongClick(view: View, position: Int, data: T) : Boolean

     override fun getItemViewType(position: Int): Int {
          //在Adapter中重写该方法，根据条件返回不同的值例如 0, 1, 2
          //            return 0;
          return super.getItemViewType(position)
     }

     fun getItemData(position: Int) = list[position]

     inner class ViewHolder(var binding: ViewDataBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
          init {
               binding.root.setOnClickListener(this@BaseRecyclerViewDataBindingAdapter)
               binding.root.setOnLongClickListener(this@BaseRecyclerViewDataBindingAdapter)
               initViewHolder(this)
          }
     }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val v = LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
          val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutID, parent, false)
          val viewHolder = ViewHolder(binding)
          if (viewType == 0) {
               //根據不同的 viewType 改變 view 風格
          }
          return viewHolder
     }

     override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
          viewHolder.binding.root.tag = position
          val viewType = viewHolder.itemViewType

          val data = list[position]

          onBindViewHolder(viewHolder , position , data)
     }

     override fun getItemCount(): Int {
          return list.size
     }

     fun getItem(position: Int): T {
          return list[position]
     }

     fun addItem(list: List<T>) {
          this.list =  list as MutableList<T>
     }

     fun addItem(list: ArrayList<T>) {
          this.list = list
     }

     fun addItem(data: T) {
          list.add(data)
     }

     fun clear(data: T) {
          list.clear()
     }


     override fun onClick(v: View) {
          onItemClick(v, v.tag as Int, getItem(v.tag as Int))
     }

     override fun onLongClick(v: View): Boolean {
          return onItemLongClick(v, v.tag as Int, getItem(v.tag as Int))
     }
}