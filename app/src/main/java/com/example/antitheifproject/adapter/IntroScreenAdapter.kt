package com.example.antitheifproject.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.antitheftalarm.dont.touch.phone.finder.databinding.IntroLayoutBinding
import com.example.antitheifproject.utilities.introDetailText
import com.example.antitheifproject.utilities.introHeading
import com.example.antitheifproject.utilities.slideImages

class IntroScreenAdapter(private val context: Context) : PagerAdapter() {

    override fun getCount(): Int {
        return introHeading.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Use the generated binding class
        val binding = IntroLayoutBinding.inflate(LayoutInflater.from(context), container, false)
        val viewHolder = ViewHolder(binding)

        with(viewHolder) {
            imageView.setImageResource(slideImages[position])
            textViewHeading.text = introHeading[position]
            textViewContent.text = introDetailText[position]
        }

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private class ViewHolder(val binding: IntroLayoutBinding) {
        val imageView = binding.sliderImage
        val textViewHeading = binding.sliderHeading
        val textViewContent = binding.sliderDesc
    }
}
