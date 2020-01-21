package com.example.hackathon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.show_account_detail_fragment.*
import org.json.JSONObject


class ShowAccountDetailActivity : AppCompatActivity() {
    var scannedResult: String = ""
    var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = getIntent().getExtras();
        val walletDTOJsonString = extras?.getString("test")

        setContentView(R.layout.show_account_detail_fragment)

        if (!walletDTOJsonString.isNullOrEmpty()) {
            var getJson = JSONObject(walletDTOJsonString)

            address = getJson.getString("address")
            var userId = getJson.getString("userId")
            var balance = getJson.getJSONObject("balance")
            var available = balance.getLong("available")
            var estimated = balance.getLong("estimated")

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
                BitcoinUtils().startQRCodeActivity(this,address)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {

            if (result.contents != null) {
                println("towa scan result: " + result.contents)
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
