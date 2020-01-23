package com.example.hackathon

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon.model.WalletResponse
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.show_account_detail_fragment.*
import org.json.JSONObject
import java.util.concurrent.TimeoutException


class ShowAccountDetailActivity : AppCompatActivity() {
    var scannedResult: String = ""
    var address: String = ""

    var userId :String? = null
    var balance :String? = null
    var available :String? = null
    var estimated :String? = null

    class FetchWalletTask(private var context: Context, userId: String) : BitcoinUtils.WalletBaseTask(context, userId) {
        lateinit var dialog: AlertDialog

        companion object {

            fun startAccountDetailActivity(context: Context, walletDTOJSON: String) {
                Intent(context, ShowAccountDetailActivity::class.java).apply {
                    this.putExtra("test", walletDTOJSON)
                    context.startActivity(this)
                }
            }
        }

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

            println("towa wallet reppponse: " + walletResponse.toString())

            println("towa wallet reppponse cod: " + walletResponse.statusCode)

            if (walletResponse.statusCode == 200) {
                walletResponse.responseString.let {
                    println("towa refresh repsonse: " + it)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = getIntent().getExtras();
        val walletDTOJsonString = extras?.getString("test")

        fun startQRCodeActivity(context: Context, bitcoinAddress: String) {
            println("towa bitcoin address" + bitcoinAddress)
            Intent(context, ShowQRCodeActivity::class.java).apply {
                this.putExtra("bitcoinaddress", bitcoinAddress)
                context.startActivity(this)
            }
        }

        setContentView(R.layout.show_account_detail_fragment)

        if (!walletDTOJsonString.isNullOrEmpty()) {
            var getJson = JSONObject(walletDTOJsonString)

            address = getJson.getString("address")
            userId = getJson.getString("userId")
            var balance = getJson.getJSONObject("balance")
            var available = balance.getString("available")
            var estimated = balance.getString("estimated")



            user_name.text = userId
            account_details.text = address
            balance_data.text = available.toString()
        }

        pay.setOnClickListener {
            run {
                IntentIntegrator(this).initiateScan();
            }
        }

        receive.setOnClickListener{
            run {
                startQRCodeActivity(this,address)
            }
        }

        refresh.setOnClickListener {
            let {
                println("towa user id " + userId)
                userId?.let{
                    FetchWalletTask(this, userId!!).execute()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {

            if (result.contents != null) {
                println("towa scan result: " + result.contents + "request code: " + requestCode)
                scannedResult = result.contents
            } else {
                println("towa  scan failed")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        outState?.putString("scannedResult", scannedResult)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            it?.let {
                scannedResult = it.getString("scannedResult").toString()
                println("towa scanned reulst " + scannedResult)
            }
        }
    }
}
