package com.leoo.hdwallpaperx

import android.Manifest
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_detail_image.*
import java.io.File
import java.io.IOException


/**
 * An leoo full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class DetailImageActivity : AppCompatActivity() {
    private var url = ""
    private var STORAGE_PERMISSION_DOWNLOAD_CODE = 1000
    private var STORAGE_PERMISSION_SET_BACKGROUND_CODE = 2000
    private var STORAGE_PERMISSION_SHARE_CODE = 3000
    lateinit var mAdView: AdView
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_detail_image)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        mAdView = findViewById(R.id.adView_detailImage)
        progress = findViewById(R.id.progress)
        progress.visibility = View.INVISIBLE

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        val extras = intent.extras ?: return
        url = extras.getString("url").toString()

        val reference =
            url.let { FirebaseStorage.getInstance().getReference(it) }
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

        reference.downloadUrl.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
            Glide.with(this)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOptions)
                .thumbnail(0.1f)
                .into(imageDetail)
        })

        setBackground.setOnClickListener {
            setBackgroundEvent()
        }
        share.setOnClickListener(View.OnClickListener {
            shareImage()
        })
        downloadBtn.setOnClickListener {
            downloadEvent()
        }
        close.setOnClickListener{
            closeEvent()
        }

        noAds.setOnClickListener{
//            noAds()
        }

        info.setOnClickListener{
//            info()
        }
    }

    private fun shareImage() {
        progress.visibility = View.VISIBLE
        Handler().postDelayed({
            if (checkPermission()) {
                var file: File = File(
                    Environment.getExternalStorageDirectory(),
                    Environment.DIRECTORY_DOWNLOADS + "/" + url
                )
                if (BitmapFactory.decodeFile(file.absolutePath) == null) {
                    downloadEvent()
                }
                val contentUri = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider", file)

                val shareIntent = Intent()

                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "image/jpeg"
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                val chooser = Intent.createChooser(shareIntent, "Share File")

                val resInfoList: List<ResolveInfo> = this.packageManager
                    .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

                for (resolveInfo in resInfoList) {
                    val packageName: String = resolveInfo.activityInfo.packageName
                    grantUriPermission(
                        packageName,
                        contentUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                startActivity(chooser)
            } else {
                Toast.makeText(this, "Cannot set background!", Toast.LENGTH_LONG).show()
            }
            progress.visibility = View.GONE
        }, 1500)
    }

    private fun setBackgroundEvent() {
        progress.visibility = View.VISIBLE
        Toast.makeText(this, "Setting Wallpaper...", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({
            if (checkPermission()) {
                var file: File = File(
                    Environment.getExternalStorageDirectory(),
                    Environment.DIRECTORY_DOWNLOADS + "/" + url
                )
                if (BitmapFactory.decodeFile(file.absolutePath) == null) {
                    downloadEvent()
                }
                var bitmap: Bitmap = BitmapFactory.decodeFile(file.absolutePath)
                var manager: WallpaperManager = WallpaperManager.getInstance(applicationContext)
                try {
                    manager.setBitmap(bitmap)
                    Toast.makeText(this, "Setting Completed!", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(this, "Cannot set background!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Permission Denied!! Please set permission.", Toast.LENGTH_LONG).show()
            }
            progress.visibility = View.GONE
        }, 1500)
    }

    private fun closeEvent() {
        onBackPressed()
    }

    private fun downloadEvent() {
        progress.visibility = View.VISIBLE
        Handler().postDelayed({
            if (checkPermission()) {
                var file: File = File(
                    Environment.getExternalStorageDirectory(),
                    Environment.DIRECTORY_DOWNLOADS + "/" + url
                )
                if (BitmapFactory.decodeFile(file.absolutePath) != null) {
                    Toast.makeText(this, "You're already download this image!", Toast.LENGTH_LONG).show()
                } else {
                    startDownload()
                    Toast.makeText(this, "Download Completed!", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Cannot download!", Toast.LENGTH_LONG).show()
            }
            progress.visibility = View.GONE
        }, 1500)
    }

    private fun startDownload() {
        val firebaseStore = FirebaseStorage.getInstance()
        val storageRefer: StorageReference?
        val ref: StorageReference?
        storageRefer = firebaseStore.reference
        ref = storageRefer.child(url)
        ref.downloadUrl.addOnSuccessListener { uri ->
            val urlSend = uri.toString()
            prepareDownload(
                getNameImage(url),
                "jpg",
                Environment.DIRECTORY_DOWNLOADS,
                urlSend
            )
        }.addOnFailureListener {
            Toast.makeText(this, "Error download! Try again", Toast.LENGTH_LONG).show()
        }
    }

    private fun prepareDownload(
        filename: String,
        fileExtension: String,
        destinationDirect: String,
        url: String
    ) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Download!")
        request.setDescription("Downloading...")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        request.setDestinationInExternalPublicDir(
            destinationDirect,
            "$filename.$fileExtension"
        )
        downloadManager.enqueue(request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_PERMISSION_DOWNLOAD_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission popup was granted, perform download
                    startDownload()
                } else {
                    //permission popup was denied, perform download
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }

            STORAGE_PERMISSION_SET_BACKGROUND_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //permission popup was granted, perform download
                    setBackgroundEvent()
                } else {
                    //permission popup was denied, perform download
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }

            STORAGE_PERMISSION_SHARE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    //permission popup was granted, perform download
                    shareImage()
                } else {
                    //permission popup was denied, perform download
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_DOWNLOAD_CODE
                )
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_SET_BACKGROUND_CODE
                )
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_SHARE_CODE
                )
                return false
            }
        } else {
            return true
        }
        return true
    }

    private fun getNameImage(nameImage: String): String {
        var string: List<String> = nameImage.split("//")
        var nameImg: List<String> = string[string.size - 1].split(".")
        return nameImg[0]
    }
}
