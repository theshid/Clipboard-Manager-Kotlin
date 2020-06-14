package com.shid.clipboardmanagerkt.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.shid.clipboardmanagerkt.UI.FavoriteFragment
import com.shid.clipboardmanagerkt.UI.HomeFragment

class ViewPagerAdapter internal constructor(fm: FragmentManager) :
    FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val titleList: MutableList<Fragment> = ArrayList()

    private val count = 2
    override fun getItem(position: Int): Fragment {

        return when (position) {
            0 -> {
                HomeFragment()
            }
            1 -> {
                FavoriteFragment()
            }
            else -> {
                HomeFragment()
            }
        }

    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> {return "Clips"}
            1 -> {return "Favorite"}
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return count
    }

}