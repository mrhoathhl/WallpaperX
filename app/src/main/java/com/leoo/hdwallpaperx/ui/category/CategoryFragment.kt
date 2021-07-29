package com.leoo.hdwallpaperx.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.leoo.hdwallpaperx.R
import com.leoo.hdwallpaperx.adapter.ListAdultsCategoryAdapter
import com.leoo.hdwallpaperx.model.CategoryModel

class CategoryFragment : Fragment() {

    private lateinit var listCategory: RecyclerView
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_category, container, false)
        listCategory = root.findViewById(R.id.list_category)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        loadImage()
    }


    private fun loadImage() {
        listCategory.layoutManager = object :
            LinearLayoutManager(context, VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        listCategory.isNestedScrollingEnabled = false
        ViewCompat.setNestedScrollingEnabled(listCategory, false);
        listCategory.setHasFixedSize(true)
        listCategory.setItemViewCacheSize(20)
        listCategory.layoutManager = LinearLayoutManager(context)
        listCategory.layoutManager = GridLayoutManager(context, 1)
        listCategory.adapter =
            ListAdultsCategoryAdapter(
                declareData(),
                context
            )
    }

    private fun declareData(): ArrayList<CategoryModel> {
        var imageIdList = ArrayList<CategoryModel>()
        imageIdList.add(CategoryModel("Bikini", R.drawable.bikini))
        imageIdList.add(CategoryModel("Hentai", R.drawable.hentai))
        imageIdList.add(CategoryModel("Sexy", R.drawable.sexy))
        return imageIdList
    }

}
