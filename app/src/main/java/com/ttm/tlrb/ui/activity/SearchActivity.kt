package com.ttm.tlrb.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import com.ttm.tlrb.R
import com.ttm.tlrb.ui.fragment.RedBombFragment

class SearchActivity : StatusBarActivity() {
    private val mSearchFragment by lazy { RedBombFragment.newInstance(RedBombFragment.TYPE_SEARCH) }

    override fun getStatusBarColor(): Int {
        return R.color.white
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, mSearchFragment)
            commit()
        }

        findViewById<SearchView>(R.id.search_view)?.apply {
            setIconifiedByDefault(false)
            isIconified = false
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    mSearchFragment.onSearchKeyChange(newText)
                    return true
                }
            })

//            setOnCloseListener{
//                mSearchFragment.onSearchKeyChange("")
//                false
//            }

        }
    }
}