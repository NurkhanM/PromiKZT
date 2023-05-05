package product.promikz.screens.hometwo.story

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import product.promikz.AppConstants.PROGRESS_COUNT
import product.promikz.AppConstants.imagesStoryAll
import product.promikz.AppConstants.imagesStoryFollow
import product.promikz.R
import jp.shts.android.storiesprogressview.StoriesProgressView
import product.promikz.MyUtils
import product.promikz.databinding.FragmentStoryBinding

class StoryFragment : Fragment(), StoriesProgressView.StoriesListener {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    private var storiesProgressView: StoriesProgressView? = null
    private var image: ImageView? = null
    private var counter = 0

    private val durations = longArrayOf(
        500L, 1000L, 1500L, 4000L, 6000L, 8000L
    )

    var pressTime = 0L
    var limit = 500L

    private val onTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    storiesProgressView?.pause()
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    storiesProgressView?.resume()
                    return limit < now - pressTime
                }
            }
            return false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        val view = binding
        storiesProgressView = binding.root.findViewById<View>(R.id.stories) as StoriesProgressView
        storiesProgressView!!.setStoriesCount(PROGRESS_COUNT)
        storiesProgressView!!.setStoryDuration(durations[4])
        storiesProgressView!!.setStoriesListener(this)
        counter = 0
        storiesProgressView!!.startStories(counter)

        image = binding.root.findViewById<View>(R.id.image) as ImageView
        MyUtils.uGlide(requireContext(), image!!, imagesStoryAll[counter])

        // bind reverse view
        val reverse = binding.root.findViewById<View>(R.id.reverse)
        reverse.setOnClickListener { storiesProgressView!!.reverse() }
        reverse.setOnTouchListener(onTouchListener)

        // bind skip view
        val skip = binding.root.findViewById<View>(R.id.skip)
        skip.setOnClickListener { storiesProgressView!!.skip() }
        skip.setOnTouchListener(onTouchListener)


        view.btnStorySite.setOnClickListener {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(imagesStoryFollow[counter]))
                (activity as AppCompatActivity).finish()
                startActivity(browserIntent)
                (activity as AppCompatActivity).finish()
            }catch (e: ActivityNotFoundException){
                Toast.makeText(requireContext(), "Error link", Toast.LENGTH_SHORT).show()
            }
            Log.d(ContentValues.TAG, "imagesStoryFollow : $imagesStoryFollow")


        }

        return view.root
    }


    override fun onNext() {

        Glide.with(this).load(imagesStoryAll[++counter])
            .into(image!!)


    }

    override fun onPrev() {
        if (counter <= 0) {
            MyUtils.uGlide(requireContext(), image!!, imagesStoryAll[0])
        } else {
            MyUtils.uGlide(requireContext(), image!!, imagesStoryAll[--counter])
        }

    }

    override fun onComplete() {
        (activity as AppCompatActivity).finish()
    }

    override fun onDestroy() {
        storiesProgressView?.destroy()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}