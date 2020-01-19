package com.example.hackathon

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.model.WalletResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


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
            println("towa code = " + result?.statusCode + " towa string = " + result?.responseString)
        }
    }

    class FetchWalletTask(private var context: Context, userId: String) : BitcoinUtils.WalletBaseTask(context, userId) {
        lateinit var dialog:AlertDialog

        override fun showPreDialog() {
            val builder = AlertDialog.Builder(context)

            // Set the alert dialog title
            builder.setTitle("Getting wallet..")

            // Display a message on alert dialog
            builder.setMessage("PLease hold")

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
                
            }
            return walletResponse
        }

        override fun onPostExecute(walletResponse: WalletResponse) {
            dialog.dismiss()

            if (walletResponse.statusCode == 200) {
                walletResponse.responseString.apply {
                    BitcoinUtils().startAccountDetailActivity(context, walletResponse.responseString)
                }
            } else if (walletResponse.statusCode == 404) {
                //create a wallet
                val builder = AlertDialog.Builder(context)

                // Set the alert dialog title
                builder.setTitle("No wallet found for user: " + userId)

                // Display a message on alert dialog
                builder.setMessage("Would you like to create a wallet?")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("YES"){dialog, which ->
                    CreateWalletTask(context, userId).execute()
                }

                // Display a negative button on alert dialog
                builder.setNegativeButton("No"){dialog,which ->

                }

                // Finally, make the alert dialog using builder
                dialog = builder.create()

                // Display the alert dialog on app interface
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}