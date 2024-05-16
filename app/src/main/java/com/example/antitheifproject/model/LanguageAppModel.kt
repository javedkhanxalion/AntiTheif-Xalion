package com.example.antitheifproject.model

import com.antitheftalarm.dont.touch.phone.finder.R

data class LanguageAppModel(
    var country_name: String,
    var country_code: String,
    var country_flag: Int = R.drawable.clap_detection__icon,
    var check: Boolean
)