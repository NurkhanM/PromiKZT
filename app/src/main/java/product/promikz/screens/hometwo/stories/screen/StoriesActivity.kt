package product.promikz.screens.hometwo.stories.screen

import android.animation.Animator
import android.animation.ValueAnimator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import product.promikz.R
import product.promikz.databinding.ActivityStoriesBinding
import product.promikz.screens.hometwo.stories.app.StoryApp
import product.promikz.screens.hometwo.stories.customview.StoryPagerAdapter
import product.promikz.screens.hometwo.stories.data.Story
import product.promikz.screens.hometwo.stories.data.StoryUser
import product.promikz.screens.hometwo.stories.utils.CubeOutTransformer
import product.promikz.viewModels.HomeViewModel

class StoriesActivity : AppCompatActivity(),
    PageViewOperator {

    private lateinit var binding: ActivityStoriesBinding

    private lateinit var pagerAdapter: StoryPagerAdapter
    private var currentPage: Int = 0
    private var idStory: Int = -1

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val arguments = intent?.extras
        idStory = arguments?.getInt("storyID") ?: 0
        currentPage = arguments?.getInt("storyPOS") ?: 0

        viewModel.getStory()
        setUpPager()
    }

    override fun backPageView() {
        if (binding.viewPager.currentItem > 0) {
            try {
                fakeDrag(false)
            } catch (e: Exception) {
                //NO OP
            }
        }
    }

    override fun nextPageView() {
        if (binding.viewPager.currentItem + 1 < (binding.viewPager.adapter?.count ?: 0)) {
            try {
                fakeDrag(true)
            } catch (e: Exception) {
                //NO OP
            }
        } else {
            //there is no next story
            Toast.makeText(this, "All stories displayed.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpPager() {
        viewModel.myStory.observe(this) { resInfoMain ->
            if (resInfoMain.isSuccessful) {

                        val storyMainList = ArrayList<StoryUser>()
                        resInfoMain.body()?.data?.forEachIndexed  { index, storyForMain ->
                            val story = ArrayList<Story>()

                            resInfoMain.body()?.data?.get(index)?.images?.forEach { images ->
                                    story.add(
                                        Story(
                                            images.name,
                                            images.link.toString(),
                                            storyForMain.description
                                        )
                                    )
                            }

                            if (storyForMain.user.shop == null) {
                                storyMainList.add(
                                    StoryUser(
                                        storyForMain.name,
                                        storyForMain.user.img,
                                        storyForMain.views.toString(),
                                        storyForMain.id.toString(),
                                        story
                                    )
                                )
                            } else {
                                storyMainList.add(
                                    StoryUser(
                                        storyForMain.name,
                                        storyForMain.user.shop.img,
                                        storyForMain.views.toString(),
                                        storyForMain.id.toString(),
                                        story
                                    )
                                )

                        preLoadStories(storyMainList)

                        pagerAdapter = StoryPagerAdapter(
                            supportFragmentManager,
                            storyMainList
                        )
                        binding.viewPager.adapter = pagerAdapter
                        binding.viewPager.currentItem = currentPage
                        binding.viewPager.setPageTransformer(
                            true,
                            CubeOutTransformer()
                        )
                        binding.viewPager.addOnPageChangeListener(object : PageChangeListener() {
                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                                currentPage = position
                            }

                            override fun onPageScrollCanceled() {
                                currentFragment().resumeCurrentStory()
                            }
                        })
                    }
                }
            }
        }
    }


    private fun preLoadStories(storyUserList: ArrayList<StoryUser>) {
        val imageList = mutableListOf<String>()
        val videoList = mutableListOf<String>()

        storyUserList.forEach { storyUser ->
            storyUser.stories.forEach { story ->
                if (story.isVideo()) {
                    videoList.add(story.url)
                } else {
                    imageList.add(story.url)
                }
            }
        }
        preLoadVideos(videoList)
        preLoadImages(imageList)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun preLoadVideos(videoList: MutableList<String>) {
        videoList.map { data ->
            GlobalScope.async {
                val dataUri = Uri.parse(data)
                val dataSpec = DataSpec(dataUri, 0, 500 * 1024, null)
                val dataSource: DataSource =
                    DefaultDataSourceFactory(
                        applicationContext,
                        Util.getUserAgent(applicationContext, getString(R.string.app_name))
                    ).createDataSource()

                val listener =
                    CacheUtil.ProgressListener { requestLength: Long, bytesCached: Long, _: Long ->
                        val downloadPercentage = (bytesCached * 100.0
                                / requestLength)
                        Log.d("preLoadVideos", "downloadPercentage: $downloadPercentage")
                    }

                try {
                    CacheUtil.cache(
                        dataSpec,
                        StoryApp.simpleCache,
                        CacheUtil.DEFAULT_CACHE_KEY_FACTORY,
                        dataSource,
                        listener,
                        null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun preLoadImages(imageList: MutableList<String>) {
        imageList.forEach { imageStory ->
            Glide.with(this).load(imageStory).preload()
        }
    }

    private fun currentFragment(): StoryDisplayFragment {
        return pagerAdapter.findFragmentByPosition(
            binding.viewPager,
            currentPage
        ) as StoryDisplayFragment
    }

    /**
     * Change ViewPage sliding programmatically(not using reflection).
     * https://tech.dely.jp/entry/2018/12/13/110000
     * What for?
     * setCurrentItem(int, boolean) changes too fast. And it cannot set animation duration.
     */
    private var prevDragPosition = 0

    private fun fakeDrag(forward: Boolean) {
        if (prevDragPosition == 0 && binding.viewPager.beginFakeDrag()) {
            ValueAnimator.ofInt(0, binding.viewPager.width).apply {
                duration = 400L
                interpolator = FastOutSlowInInterpolator()
                addListener(object : Animator.AnimatorListener {

                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        removeAllUpdateListeners()
                        if (binding.viewPager.isFakeDragging) {
                            binding.viewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        removeAllUpdateListeners()
                        if (binding.viewPager.isFakeDragging) {
                            binding.viewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationRepeat(animation: Animator) {}
                })
                addUpdateListener {
                    if (!binding.viewPager.isFakeDragging) return@addUpdateListener
                    val dragPosition: Int = it.animatedValue as Int
                    val dragOffset: Float =
                        ((dragPosition - prevDragPosition) * if (forward) -1 else 1).toFloat()
                    prevDragPosition = dragPosition
                    binding.viewPager.fakeDragBy(dragOffset)
                }
            }.start()
        }
    }

    companion object {
        val progressState = SparseIntArray()
    }
}
