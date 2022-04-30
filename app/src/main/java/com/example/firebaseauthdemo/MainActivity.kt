package com.example.firebaseauthdemo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkInternetConnection()
        setContentView(R.layout.activity_main)

        val emailId = intent.getStringExtra("email_id")

        tv_password_id.text = emailId

        btn_logout.setOnClickListener {
            showProgressDialog()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            hideProgressDialog()
        }

    }

    fun showProgressDialog() {
        // setting the var to dialog of this activity
        mProgressDialog = Dialog(this)
        // setting the content
        mProgressDialog.setContentView(R.layout.dialog_progress)
        // showing the dialog
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun checkInternetConnection() {
        // checking for the internet connection...
        val connect: ConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connect.activeNetworkInfo
        val isCon: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (!isCon) {
            startActivity(Intent(this, RefreshActivity::class.java))
            finish()
            Toast.makeText(this, "Your internet is turned off!!", Toast.LENGTH_LONG).show()
        }
    }
}