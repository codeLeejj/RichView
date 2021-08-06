package com.lee.richview.banner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import com.android.libview.view.banner.Banner
import com.android.libview.view.banner.core.BannerAdapter
import com.android.libview.view.banner.core.IAdapter
import com.bumptech.glide.Glide
import com.lee.richview.R

class BannerDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_demo)

        initBanner()
    }

    private var urls: List<BannerBo>? = null
    lateinit var banner: Banner<BannerBo>
    private fun initBanner() {
        banner = findViewById(R.id.banner)
        banner.setLifecycle(lifecycle)
        banner.setAdapter(object : IAdapter<BannerBo>(urls?.toMutableList()) {
            override fun getViewId(viewType: Int): Int = R.layout.item_banner_image
            override fun fillView(holder: BannerAdapter.BannerViewHolder, t: BannerBo) {
                Glide.with(this@BannerDemoActivity).load(t.url)
                    .into(holder.getView<AppCompatImageView>(R.id.aiv))
            }
        })
        //模拟网络延迟
        banner.postDelayed({
            banner.update(
                arrayOf(
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera1.jpg"),
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera3.jpg"),
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera4.jpg"),
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera5.jpg"),
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera2.jpg"),
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera6.jpg"),
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera7.jpg"),
                    BannerBo("https://www.devio.org/img/beauty_camera/beauty_camera8.jpeg")
                ).toMutableList()
            )
        }, 2_000)
        //测试轮播开关
        banner.postDelayed({
            banner.setLoop(false)
        }, 10_000)//要滚动到最后才能看到效果

        banner.postDelayed({
            banner.setLoop(true)
        }, 60_000)

        //测试自动播放开关
    }
}