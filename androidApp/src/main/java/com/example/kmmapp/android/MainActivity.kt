package com.example.kmmapp.android

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions = "email, user_photos, user_posts"
        val loginButton = findViewById<View>(R.id.loginButton) as LoginButton
        loginButton.setPermissions(listOf(permissions))
        val parameters = Bundle()
        val callbackManager = CallbackManager.Factory.create()


        var fbId: String? = null
        var fbEmail: String? = null
        var fbName: String? = null
        val listOfImages = mutableListOf<ImagesModel>()

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {

            override fun onSuccess(result: LoginResult?) {
                findViewById<TextView>(R.id.accessToken).text =
                    "${result?.accessToken} ${result?.authenticationToken}  ${result?.recentlyGrantedPermissions}  ${result?.recentlyDeniedPermissions}"

                val request = GraphRequest.newMeRequest(result?.accessToken) { result, response ->
                    if (result != null) {
                        fbName = if (result.has("name")) result.getString("name") else ""
                        fbId = if (result.has("id")) result.getString("id") else ""
                        fbEmail = if (result.has("email")) result.getString("email") else ""


                        val obj: JSONArray = result.getJSONObject("albums").getJSONArray("data")
                        var keys = obj.length() - 1
                        while (keys != 0) {
                            val link = obj.optJSONObject(keys).getJSONObject("picture").getJSONObject("data")
                                .getString("url")
                            val id = obj.optJSONObject(keys).getString("id")
                            listOfImages.add(ImagesModel(id, link))
                            keys -= 1

                            findViewById<TextView>(R.id.accessToken).text = listOfImages.toString()
                        }
                    }
                }


                parameters.putString("fields", "name,id,photos,birthday,hometown,albums{picture},gender,email")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                findViewById<TextView>(R.id.accessToken).text = "Отмена"
            }

            override fun onError(exception: FacebookException) {
                findViewById<TextView>(R.id.accessToken).text = "Ошибка"
            }
        })
    }
}

data class ImagesModel(
    val id: String,
    val url: String
)
