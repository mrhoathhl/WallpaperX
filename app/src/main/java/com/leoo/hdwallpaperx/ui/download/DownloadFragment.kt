package com.leoo.hdwallpaperx.ui.download

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.leoo.hdwallpaperx.R


class DownloadFragment : Fragment(), View.OnClickListener{
    private lateinit var versionApp: TextView
    private lateinit var upgrade: LinearLayout
    private lateinit var restart: LinearLayout
    private lateinit var policy: LinearLayout
    private lateinit var moreApp: LinearLayout
    private lateinit var rate: LinearLayout
    private lateinit var share: LinearLayout
    private lateinit var facebook: LinearLayout
    private lateinit var twitter: LinearLayout
    private lateinit var instagram: LinearLayout
    private lateinit var pinterest: LinearLayout
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val versionName = requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName

        mappingId(root)
        clickListenerDefine()

        versionApp.text = "Version: $versionName"
        return root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.upgrade -> {

            }
            R.id.restart -> {
                context?.cacheDir?.deleteRecursively()
                val i: Intent? = requireContext().packageManager
                    .getLaunchIntentForPackage(requireContext().packageName)
                i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)
            }
            R.id.policy -> {

            }
            R.id.moreApp -> {

            }
            R.id.rate -> {

            }
            R.id.share -> {

            }
            R.id.facebook -> {
                openSocialNetwork("http://www.facebook.com", "facebook.com")
            }
            R.id.instagram -> {
                openSocialNetwork("http://www.instagram.com", "instagram.com")
            }
            R.id.twitter -> {
                openSocialNetwork("http://www.twitter.com", "twitter.com")
            }
            R.id.pinterest -> {
                openSocialNetwork("http://www.pinterest.com", "pinterest.com")
            }
        }
    }

    private fun openSocialNetwork(url: String, urlErr: String){
        try {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent);
        } catch (e: Exception) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(urlErr)
                )
            )
        }
    }

    private fun mappingId(root: View) {
        versionApp = root.findViewById(R.id.versionApp)
        upgrade = root.findViewById(R.id.upgrade)
        restart = root.findViewById(R.id.restart)
        policy = root.findViewById(R.id.policy)
        moreApp = root.findViewById(R.id.moreApp)
        rate = root.findViewById(R.id.rate)
        share = root.findViewById(R.id.share)
        facebook = root.findViewById(R.id.facebook)
        twitter = root.findViewById(R.id.twitter)
        instagram = root.findViewById(R.id.instagram)
        pinterest = root.findViewById(R.id.pinterest)
    }

    private fun clickListenerDefine (){
        upgrade.setOnClickListener(this)
        restart.setOnClickListener(this)
        policy.setOnClickListener(this)
        moreApp.setOnClickListener(this)
        rate.setOnClickListener(this)
        share.setOnClickListener(this)
        facebook.setOnClickListener(this)
        twitter.setOnClickListener(this)
        instagram.setOnClickListener(this)
        pinterest.setOnClickListener(this)
    }
}
