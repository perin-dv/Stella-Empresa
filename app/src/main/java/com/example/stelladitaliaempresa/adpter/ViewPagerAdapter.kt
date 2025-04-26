package com.example.stelladitaliaempresa.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stelladitaliaempresa.fragment.PedidosFragment
import com.example.stelladitaliaempresa.fragment.ProdutosFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PedidosFragment()
            1 -> ProdutosFragment()
            else -> PedidosFragment()
        }
    }
}
