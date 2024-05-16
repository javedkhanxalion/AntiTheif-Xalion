package com.example.antitheifproject

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antitheftalarm.dont.touch.phone.finder.databinding.MainActivityApplicationBinding
import com.example.antitheifproject.utilities.setStatusBar

class MainActivity : AppCompatActivity() {

    private var binding: MainActivityApplicationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityApplicationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setStatusBar()
        }
    }

}
