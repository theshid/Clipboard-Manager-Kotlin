package com.shid.clipboardmanagerkt.UI

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.shid.clipboardmanagerkt.Adapters.ViewPagerAdapter
import com.shid.clipboardmanagerkt.R
import com.shid.clipboardmanagerkt.Utils.BounceInterpolator
import com.shid.clipboardmanagerkt.Utils.SharedPref


class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var appBarLayout: AppBarLayout
    private val TAG: String = MainActivity::class.java.simpleName
    private lateinit var sharedPref: SharedPref
    private lateinit var imageDay: ImageView
    private lateinit var imageNight: ImageView
    private lateinit var btnBulbOff: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        checkPrefNight()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUI()
        checkPref()
        setButtonAnimation()
        btnClickListener()
    }

    private fun setButtonAnimation() {
        val animation:Animation = AnimationUtils.loadAnimation(this,R.anim.bounce)
        var interpolator = BounceInterpolator()
        animation.setInterpolator(interpolator)
        btnBulbOff.startAnimation(animation)
    }

    private fun setUI() {
        tabLayout = findViewById(R.id.tablayout_id)
        viewPager = findViewById(R.id.view_pager)
        appBarLayout = findViewById(R.id.app_bar_id)
        imageDay = findViewById(R.id.image_zoro)
        imageNight = findViewById(R.id.image_zoro_night)
        btnBulbOff = findViewById(R.id.btn_bulbOff)



        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun checkPrefNight() {
        sharedPref = SharedPref(this)
        if (sharedPref.loadNightMode()) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.DayTheme)
        }
    }

    private fun recreateApp() {
        this.recreate()
    }

    private fun checkPref() {
        sharedPref = SharedPref(this)
        if (sharedPref.loadNightMode()) {
            setTheme(R.style.DarkTheme)

            imageNight.setVisibility(View.VISIBLE)
            imageDay.setVisibility(View.GONE)
            btnBulbOff.setBackgroundResource(R.drawable.bulb_on)
            sharedPref.setButtonState(false)
            //sparkButton.setChecked(true)
        } else {
            setTheme(R.style.DayTheme)
            imageDay.setVisibility(View.VISIBLE)
            imageNight.setVisibility(View.GONE)
            btnBulbOff.setBackgroundResource(R.drawable.bulb_off)
            sharedPref.setButtonState(true)
            // sparkButton.setChecked(false)
        }
    }

    private fun btnClickListener() {
        btnBulbOff.setOnClickListener {
            Log.d(TAG, "nightMode status" + sharedPref.loadNightMode())
            Log.d(TAG, "nightMode status2" + sharedPref.loadButtonState())
            var bulb_state: Boolean = sharedPref.loadButtonState()
            if (bulb_state) {
                sharedPref.setNightMode(true)
                recreateApp()
            } else{
                sharedPref.setNightMode(false)
                recreateApp()
            }
        }
    }
}