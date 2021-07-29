package com.leoo.hdwallpaperx.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.leoo.hdwallpaperx.R
import com.leoo.hdwallpaperx.adapter.ListImageAdapter


class HomeFragment : Fragment() {

    // The AdLoader used to load ads.
    private var adLoader: AdLoader? = null

    // List of MenuItems and native ads that populate the RecyclerView.
    private val mRecyclerViewItems: MutableList<Any> = ArrayList()

    // List of native ads that have been successfully loaded.
    private val mNativeAds: MutableList<UnifiedNativeAd> = ArrayList()
    private lateinit var database: DatabaseReference
    private lateinit var listImage: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        database = FirebaseDatabase.getInstance().reference
        listImage = root.findViewById(R.id.list_image)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listImage.setHasFixedSize(true)
        listImage.setItemViewCacheSize(20)
        listImage.layoutManager = LinearLayoutManager(context)
        listImage.layoutManager = GridLayoutManager(context, 2)
        loadImage("")
    }

    private fun loadImage(pageToken: String?) {
        val storage = FirebaseStorage.getInstance()
        val listRef = storage.getReference("common")
        // Fetch the next page of results, using the pageToken if we have one.
        val listPageTask = if (pageToken != null) {
            listRef.list(50, pageToken)
        } else {
            listRef.list(50)
        }
        listPageTask
                .addOnSuccessListener { listResult ->
                    listResult.items.forEach { item ->
                        mRecyclerViewItems.add("common/" + item.name)
                    }
                    loadNativeAds()
                    listImage.adapter = ListImageAdapter(mRecyclerViewItems as ArrayList<Any>, context)
                    listResult.pageToken?.let {
                        loadImage(it)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        context,
                        "Listener was cancelled", Toast.LENGTH_LONG
                    ).show()
                }
    }

    private fun insertAdsInMenuItems() {
        if (mNativeAds.size <= 0) {
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
        val builder = AdLoader.Builder(context, getString(R.string.admod_id_native))
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
