package com.example.hackathon

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.model.WalletResponse
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.concurrent.TimeoutException


class MainActivity : AppCompatActivity() {

    class CreateWalletTask(context: Context, userId: String) : BitcoinUtils.WalletBaseTask(context, userId) {

        override fun showPreDialog() {

        }

        override fun onPreExecute() {
            println("towa creating wallet")
        }

        override fun doInBackground(vararg params: String?): WalletResponse{
            try {
                val response = BitcoinUtils().createWallet(userId)

                walletResponse.statusCode = response.code()

                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        walletResponse.responseString = response.body()?.string().toString()
                    }
                }
            } catch (e:Exception) {

            }
            return walletResponse
        }

        override fun onPostExecute(result: WalletResponse?) {
            println("towa create wallet result: code = " + result?.statusCode + " towa string = " + result?.responseString)
        }
    }

    class FetchWalletTask(private var context: Context, userId: String) : BitcoinUtils.WalletBaseTask(context, userId) {
        lateinit var dialog:AlertDialog

        override fun showPreDialog() {
            val builder = AlertDialog.Builder(context)

            // Set the alert dialog title
            builder.setTitle("Getting wallet..")

            // Display a message on alert dialog
            builder.setMessage("Please hold")

            // Finally, make the alert dialog using builder
            dialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()
        }

        override fun onPreExecute() {
            showPreDialog()

            //BitcoinUtils().createWallet(id)
        }

        override fun doInBackground(vararg params: String?): WalletResponse {
            try {
                val response = BitcoinUtils().getWallet(userId)

                walletResponse.statusCode = response.code()

                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        walletResponse.responseString = response.body()?.string().toString()
                    }
                }
            } catch (e:Exception) {
                if (e is TimeoutException) {
                    System.out.println("towa timeout? " + e.message)
                } else {
                    println("towa some kind of exceptiooon: " + e.message)
                }
            }
            return walletResponse
        }

        override fun onPostExecute(walletResponse: WalletResponse) {
            dialog.dismiss()

            println("towa wallet reponse: " + walletResponse.toString())

            if (walletResponse.statusCode == 200) {
                walletResponse.responseString.apply {
                    BitcoinUtils().startAccountDetailActivity(context, walletResponse.responseString)
                }
            } else if (walletResponse.statusCode == 404) {
                //create a wallet
                val builder = AlertDialog.Builder(context)

                builder.setTitle("No wallet found for user: " + userId)
                builder.setMessage("Would you like to create a wallet?")
                builder.setPositiveButton("YES"){dialog, which ->
                    CreateWalletTask(context, userId).execute()
                }

                // Display a negative button on alert dialog
                builder.setNegativeButton("No"){dialog, which ->

                }
                dialog = builder.create()
                dialog.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        user_id.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                login_button.isEnabled = user_id.text.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {


            }
        })

        login_button.setOnClickListener {
            FetchWalletTask(this,user_id.text.toString()).execute()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {

            if (result.contents != null) {
                println("towa scan result: " + result.contents)
            } else {
                println("towa  scan failed")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        println("towa on options item selectedd...")
        BitcoinUtils().startQRCodeActivity(this, "arseholes")
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
