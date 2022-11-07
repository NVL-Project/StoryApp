package com.nvl.storyapp.view.detail

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.nvl.storyapp.R
import com.nvl.storyapp.data.server.response.StoryItem
import com.nvl.storyapp.databinding.ActivityDetailBinding
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.detail_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_PERSON, StoryItem::class.java) as StoryItem
        } else {
            intent.getParcelableExtra<StoryItem>(EXTRA_PERSON) as StoryItem
        }
        val tmZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        tmZ.timeZone = TimeZone.getTimeZone("ICT")
        val time = tmZ.parse(story.createdAt)?.time
        val prettyTime = PrettyTime(Locale.getDefault())
        val ago = prettyTime.format(time?.let { Date(it) })
        with(binding){
            Glide.with(applicationContext)
                .load(story.photoUrl)
                .into(imgStory)
            tvStoryName.text = story.name
            tvCreatedAt.text = ago
            tvDesc.text = story.description
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        binding
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_PERSON = "extra_person"
    }
}