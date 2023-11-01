package io.github.matirosen.dogit.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.matirosen.dogit.R
import io.github.matirosen.dogit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }
}