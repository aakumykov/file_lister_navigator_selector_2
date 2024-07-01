package com.github.aakumykov.file_lister_navigator_selector_demo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.aakumykov.file_lister_navigator_selector_demo.databinding.ActivityMainBinding
import com.github.aakumykov.file_lister_navigator_selector_demo.fragments.demo.DemoFragment
import com.github.aakumykov.storage_access_helper.StorageAccessHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (null == savedInstanceState) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, DemoFragment.create(), DemoFragment.TAG)
                .commit()
        }

        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionAppProperties -> {
                openAppProperties()
                true
            }
            R.id.actionCloseApp -> {
                closeApp()
                true
            }
            R.id.actionStorageAccess -> {
                StorageAccessHelper.openStorageAccessSettings(this)
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun closeApp() {
        finish()
    }

    private fun openAppProperties() {
        val uri = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
        startActivity(intent)
    }
}