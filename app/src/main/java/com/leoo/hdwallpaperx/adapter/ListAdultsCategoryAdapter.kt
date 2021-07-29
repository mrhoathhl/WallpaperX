package com.leoo.hdwallpaperx.adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.leoo.hdwallpaperx.ListImageActivity
import com.leoo.hdwallpaperx.R
import com.leoo.hdwallpaperx.model.CategoryModel
import kotlinx.android.synthetic.main.category_items.view.*

class ListAdultsCategoryAdapter(
    private val items: ArrayList<CategoryModel>,
    val context: Context?
) : RecyclerView.Adapter<ListAdultsCategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.category_items, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mInterstitialAd = InterstitialAd(context)
        Handler().postDelayed({
            mInterstitialAd.adUnitId = context?.resources?.getString(R.string.admod_id_interstitial_2)
            mInterstitialAd.loadAd(AdRequest.Builder().build())
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdClosed() {
                    val intent = Intent(context, ListImageActivity::class.java)
                    intent.putExtra("url", "adults/" + items[position].name.toLowerCase())
                    context?.startActivity(intent)
                }
            }
        }, 1500)

        context?.let { Glide.with(it)
            .load(items[position].src)
            .thumbnail(0.1f)
            .into(holder.imageView)
        }

        holder.imageView.setOnClickListener {
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                val intent = Intent(context, ListImageActivity::class.java)
                intent.putExtra("url", "adults/" + items[position].name.toLowerCase())
                context?.startActivity(intent)
            }
        }

        holder.cate_title.text = items[position].name
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        var imageView: ImageView = view.imageView
        var cate_title: TextView = view.cate_title
    }

}
