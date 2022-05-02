package com.example.firebaseauthdemo

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    // var for the progress bar
    private lateinit var mProgressDialog: Dialog

    // vars for the auth
    lateinit var auth:FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // for simplification
        auth = FirebaseAuth.getInstance()

        // login button
        btn_otp_login.setOnClickListener {
            val mobileNum = et_login_number.text
            if (mobileNum.isEmpty() || mobileNum.length < 10 || mobileNum.length > 10) {
                Toast.makeText(this, "INVALID", Toast.LENGTH_SHORT).show()
            } else {
                login()
            }
        }

        // Callback function for Phone Auth
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // after the otp verification
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val intent = Intent(this@OtpActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("email_id", FirebaseAuth.getInstance().currentUser!!.phoneNumber)
                startActivity(intent)
                hideProgressDialog()
                finish()
            }

            // if verification failed
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_LONG).show()
                hideProgressDialog()
            }

            // when OTP send to phone
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                hideProgressDialog()

                // when 2nd time click on the login btn
                btn_otp_login.setOnClickListener{
                    showProgressDialog()
                    var otp = et_otp.text.toString().trim()
                    if(otp.isNotEmpty()){
                        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                            storedVerificationId.toString(), otp)
                        signInWithPhoneAuthCredential(credential)
                    } else{
                        Toast.makeText(applicationContext, "Enter OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun login() {
        showProgressDialog()
        til_otp.visibility = View.VISIBLE
        btn_otp_login.text = "LOGIN"
        var number = et_login_number.text.toString().trim()

        if(number.isNotEmpty()){
            number = "+91$number"
            sendVerificationCode(number)
        } else{
            Toast.makeText(this,"Enter mobile number",Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@OtpActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("email_id", FirebaseAuth.getInstance().currentUser!!.phoneNumber)
                    startActivity(intent)
                    hideProgressDialog()
                    finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
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

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}