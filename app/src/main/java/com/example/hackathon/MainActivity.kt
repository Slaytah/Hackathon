package com.example.hackathon

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    class FetchWalletTask(private var context: Context?, userId: String) : AsyncTask<String, String, String>() {

        var id = userId

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {

            var response = BitcoinUtils().getWallet(id)

            var walletDTOJsonString: String = ""


            if (response.isSuccessful) {
                System.out.println("towa success")
                if (response.code() == 200) {
                    System.out.println("towa super success")
                    walletDTOJsonString = response?.body()?.string().toString()
                    System.out.println("towa result = " + walletDTOJsonString)

                    var getJson = JSONObject(walletDTOJsonString)

                    var address = getJson.getString("address")
                    var userId = getJson.getString("userId")
                    var balance = getJson.getJSONObject("balance")
                    var available = balance.getLong("available")
                    var estimated = balance.getLong("estimated")

                    var balanceDTO = BitcoinUtils.BalanceDTO(available, estimated)

                    var wallet = BitcoinUtils.WalletDTO(balanceDTO, address, userId)
                } else if (response.code() == 404 || response.code() == 500) {
                    //create a wallet
                    //BitcoinUtils().createWallet(id)
                } else {
                    System.out.println("towa responsse code: " + response.code())
                }
            } else {
                System.out.println("towa not success")
                System.out.println("towa create result " + response)

                if (response.code() == 404) {
                    System.out.println("towa shoudl create a walle")
                    var response2 = BitcoinUtils().createWallet(id)

                    System.out.println("towa create result " + response2)

                }

                //response = BitcoinUtils().createWallet(id)


            }
            return walletDTOJsonString.toString()
        }


        override fun onPostExecute(walletDTOJsonString: String) {
            super.onPostExecute(walletDTOJsonString)

            if (!walletDTOJsonString.isNullOrEmpty()) {
                walletDTOJsonString?.apply {
                    context?.let {
                        BitcoinUtils().startAccountDetailActivity(it, walletDTOJsonString)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //UI components
        //login_button.isEnabled = false


        user_id.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                login_button.isEnabled = !user_id.text.isEmpty()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {


            }
        })

        login_button.setOnClickListener {
            var task = FetchWalletTask(this,user_id.text.toString())
            task.execute()
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
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