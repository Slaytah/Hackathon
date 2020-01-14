package com.example.hackathon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.show_account_detail_fragment.*
import org.json.JSONObject


class ShowAccountDetailActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.show_account_detail_fragment)

        var detector: BarcodeDetector? = null

        val extras = getIntent().getExtras();

        val walletDTOJsonString = extras?.getString("test")

        if (!walletDTOJsonString.isNullOrEmpty()) {


            var getJson = JSONObject(walletDTOJsonString)

            var address = getJson.getString("address")
            var userId = getJson.getString("userId")
            var balance = getJson.getJSONObject("balance")
            var available = balance.getLong("available")
            var estimated = balance.getLong("estimated")


            System.out.println("towa message = " + userId)

            user_name.text = userId
            account_details.text = address
            balance_data.text = available.toString()
        }

        pay.setOnClickListener({
            //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_PERMISSION)
        })

    }
}
