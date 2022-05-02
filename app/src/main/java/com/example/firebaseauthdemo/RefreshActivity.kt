package com.example.firebaseauthdemo

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_refresh.*

class RefreshActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refresh)

        btn_refresh.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            showProgressDialog()
            finish()
        }
    }

    private fun showProgressDialog() {
        // setting the var to dialog of this activity
        mProgressDialog = Dialog(this)
        // setting the content
        mProgressDialog.setContentView(R.layout.dialog_progress)
        // showing the dialog
        mProgressDialog.show()
    }
}