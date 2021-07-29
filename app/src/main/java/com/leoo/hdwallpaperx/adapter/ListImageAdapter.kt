package com.leoo.hdwallpaperx.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.firebase.storage.FirebaseStorage
import com.leoo.hdwallpaperx.DetailImageActivity
import com.leoo.hdwallpaperx.R
import kotlinx.android.synthetic.main.image_items.view.*
import java.util.*


class ListImageAdapter(private val items: ArrayList<Any>, val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val unifiedNativeLayoutView = LayoutInflater.from(
                    parent.context).inflate(R.layout.native_ads,
                    parent, false)
                UnifiedNativeAdViewHolder(unifiedNativeLayoutView)
            }
            MENU_ITEM_VIEW_TYPE -> {
                val menuItemLayoutView = LayoutInflater.from(parent.context).inflate(
                    R.layout.image_items, parent, false)
                ItemImageViewHolder(menuItemLayoutView)
            }
            else -> {
                val menuItemLayoutView = LayoutInflater.from(parent.context).inflate(
                    R.layout.image_items, parent, false)
                ItemImageViewHolder(menuItemLayoutView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        val mInterstitialAd = InterstitialAd(context)
        when (getItemViewType(position)) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val nativeAd = items[position] as UnifiedNativeAd
                populateNativeAdView(nativeAd, (holder as UnifiedNativeAdViewHolder).getAdView()!!)
            }
            else -> {
                val reference = FirebaseStorage.getInstance().getReference(items[position].toString())
                Handler().postDelayed({
                    mInterstitialAd.adUnitId = context?.resources?.getString(R.string.admod_id_interstitial_2)
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                    mInterstitialAd.adListener = object : AdListener() {
                        override fun onAdClosed() {
                            val intent = Intent(context, DetailImageActivity::class.java)
                            intent.putExtra("url", items[position].toString())
                            context?.startActivity(intent)
                        }
                    }
                }, 1500)
                val menuItemHolder = holder as ItemImageViewHolder
                reference.downloadUrl.addOnSuccessListener { uri ->
                    context?.let {
                        Glide.with(it)
                            .load(uri)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(requestOptions)
                            .thumbnail(0.1f)
                            .into(menuItemHolder.imageView)
                    }
                }
                menuItemHolder.imageView.setOnClickListener {
                    if (mInterstitialAd.isLoaded) {
                        mInterstitialAd.show()
                    } else {
                        val intent = Intent(context, DetailImageActivity::class.java)
                        intent.putExtra("url", items[position].toString())
                        context?.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val recyclerViewItem = items[position]
        return if (recyclerViewItem is UnifiedNativeAd) {
            UNIFIED_NATIVE_AD_VIEW_TYPE
        } else MENU_ITEM_VIEW_TYPE
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ItemImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        var imageView: ImageView = view.imageView
//        var progressImage: ProgressBar = view.progressImage
    }

    private fun populateNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction
        (adView.mediaView as MediaView).setImageScaleType(ImageView.ScaleType.FIT_CENTER)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        val icon = nativeAd.icon
        if (icon == null) {
            adView.iconView.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            (adView.priceView as TextView).text = nativeAd.price
            adView.priceView.visibility = View.VISIBLE
        }
        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd)
    }

    companion object {
        // A menu item view type.
        private const val MENU_ITEM_VIEW_TYPE = 0
        private const val UNIFIED_NATIVE_AD_VIEW_TYPE = 1
    }

}
