package com.leoo.hdwallpaperx

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.leoo.hdwallpaperx.adapter.ListImageAdapter

class ListImageActivity : AppCompatActivity() {
    lateinit var mAdView: AdView
    private lateinit var database: DatabaseReference
    private lateinit var listImageActivity: RecyclerView

    // The AdLoader used to load ads.
    private var adLoader: AdLoader? = null

    // List of MenuItems and native ads that populate the RecyclerView.
    private val mRecyclerViewItems: MutableList<Any> = ArrayList()

    // List of native ads that have been successfully loaded.
    private val mNativeAds: MutableList<UnifiedNativeAd> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_image)

        database = FirebaseDatabase.getInstance().reference
        listImageActivity = findViewById<RecyclerView>(R.id.list_image_activity)
        val extras = intent.extras ?: return
        val url = extras.getString("url")
        if (url != null) {
            loadImage(url)
        }

        Handler().postDelayed({
            mAdView = findViewById(R.id.adView_listImage)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        }, 1000)
    }

    private fun loadImage(url: String) {
        val storage = FirebaseStorage.getInstance()
        val listRef = storage.getReference(url)
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    mRecyclerViewItems.add("$url/" + item.name)
                }
                loadNativeAds()
                listImageActivity.setHasFixedSize(true)
                listImageActivity.setItemViewCacheSize(20)
                listImageActivity.layoutManager = LinearLayoutManager(this)
                listImageActivity.layoutManager = GridLayoutManager(this, 2)
                listImageActivity.adapter = ListImageAdapter(mRecyclerViewItems as java.util.ArrayList<Any>, this)
            }
            .addOnFailureListener {
            }
    }

    private fun insertAdsInMenuItems() {
        if (mNativeAds.size <= 9) {
            return
        }
        val offset = mRecyclerViewItems.size / mNativeAds.size + 1
        var index = 9
        for (ad in mNativeAds) {
            mRecyclerViewItems.add(index, ad)
            index += offset
        }
    }

    private fun loadNativeAds() {
        val builder = AdLoader.Builder(this, getString(R.string.admod_id_native))
        adLoader = builder.forUnifiedNativeAd { unifiedNativeAd -> // A native ad loaded successfully, check if the ad loader has finished loading
            // and if so, insert the ads into the list.
            mNativeAds.add(unifiedNativeAd)
            if (!adLoader!!.isLoading) {
                insertAdsInMenuItems()
            }
        }.withAdListener(
            object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    // A native ad failed to load, check if the ad loader has finished loading
                    // and if so, insert the ads into the list.
                    Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                            + " load another.")
                    if (!adLoader!!.isLoading) {
                        insertAdsInMenuItems()
                    }
                }
            }).build()
        // Load the Native Express ad.
        adLoader?.loadAds(AdRequest.Builder().build(), 5)
    }

}
