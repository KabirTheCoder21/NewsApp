package com.example.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Math.abs

class MainActivity : AppCompatActivity() {
    lateinit var adapter: NewsAdapter
    private lateinit var newsList: ViewPager2
    private var articles = mutableListOf<Article>()
    val sliderHandler = Handler()
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"
    private lateinit var shimmer: ShimmerFrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //admob created
        MobileAds.initialize(this) {}
        loadInterstitialAd()
        newsList = findViewById(R.id.newsList)
        shimmer = findViewById(R.id.shimmer_layout)
        shimmer.startShimmer()
        adapter = NewsAdapter(this@MainActivity, articles)
        this@MainActivity.newsList.adapter = adapter

        newsList.clipChildren = false
        newsList.clipToPadding = false

        newsList.offscreenPageLimit = 2
        newsList.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(object : ViewPager2.PageTransformer {

            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.16f
            }

        })
        newsList.setPageTransformer(compositePageTransformer)
        newsList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 10000)

                if (position == articles.size - 2) {
                    newsList.post(runnable)
                }

                // Show interstitial ad every time a new page is selected
                if (position % 5 == 0) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    // Load a new interstitial ad for the next time
                                    loadInterstitialAd()
                                }
                            }
                        // Show the interstitial ad
                        mInterstitialAd?.show(this@MainActivity)
                    }

                }
            }
        })

        getnews()

    }

    private fun loadInterstitialAd() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    adError.toString().let { Log.d(TAG, it) }
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })
    }

    val sliderRunnable = Runnable {
        newsList.currentItem = newsList.currentItem + 1

    }
    val runnable = Runnable {

        getnews()
    }

    private fun getnews() {
        val news: Call<News> = NewsService.newsInstance.getHeadLines("us", 1)
        news.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val news: News? = response.body()
                if (news != null) {
                    Log.d("Demo", news.toString())
                    shimmer.stopShimmer()
                    shimmer.visibility = View.GONE
                    newsList.visibility = View.VISIBLE
                    articles.addAll(news.articles)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("Demo", "Error in Fetching News", t)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 10000)
    }
}