package comalpha4.chatappwithlocation

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import comalpha4.chatappwithlocation.Models.UserModel

class LoginActivity : AppCompatActivity() {
    var reference: DatabaseReference? = null
   lateinit var signup:TextView
   lateinit var btnLogin:TextView
   lateinit var phoneNumberET:TextInputEditText
   lateinit var passwordEt:TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }
    fun init() {
        btnLogin = findViewById<TextView>(R.id.btnLogin)
        phoneNumberET = findViewById<TextInputEditText>(R.id.phoneNumberET)
        passwordEt = findViewById<TextInputEditText>(R.id.passwordEt)
        signup = findViewById<TextView>(R.id.signup)

        reference = FirebaseDatabase.getInstance().getReference("users")
        btnLogin.setOnClickListener {
            validation()
        }
        signup.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }
    fun validation() {
        if (passwordEt.getText().toString().trim { it <= ' ' } == "") {
            passwordEt.setError("enter password")
        } else if (phoneNumberET.getText().toString().trim { it <= ' ' } == "") {
            phoneNumberET.setError("enter email")
        } else {
            LoginUser()
        }
    }
  lateinit  var userModel:UserModel
    fun LoginUser() {
        val query: Query = reference!!.orderByChild("email").equalTo(phoneNumberET.getText().toString())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (data in dataSnapshot.children) {
                         userModel=UserModel(
                            data.child("email").getValue<String>(String::class.java),
                            data.child("fcm").getValue<String>(String::class.java),
                            data.child("latlng").getValue<String>(String::class.java),
                            data.child("name").getValue<String>(String::class.java),
                            data.child("password").getValue<String>(String::class.java),
                            data.child("phone").getValue<String>(String::class.java))
                       /* username = data.child("userName").getValue<String>(String::class.java)
                        email = data.child("email").getValue<String>(String::class.java)
                        password = data.child("password").getValue<String>(String::class.java)
                        fcmToken = data.child("fcmToken").getValue<String>(String::class.java)
                        userid = data.child("userId").getValue<String>(String::class.java)
                        usertype = data.child("usertype").getValue<String>(String::class.java)*/
                        //                        userInterests = data.child("interests").getValue(Map.class);
//                        Map<String, Object> map = (Map<String, Object>) data.child("interests").getValue();;
                    }
                    if (userModel.password == passwordEt.getText().toString()) {
                    /*
                        sessionMAnager.setUserid(userid)
                        sessionMAnager.setUsername(username)
                        sessionMAnager.setEmail(email)
                        sessionMAnager.setPassword(password)
                        sessionMAnager.setFcmToken(fcmToken)
                        sessionMAnager.setusertype(usertype)
                        //                        sessionMAnager.setInterset(userInterests);
                        sessionMAnager.setLogin(true)
                        //                        referenceUpdateToken = FirebaseDatabase.getInstance().getReference("users").child(userid);
                        val updateToken: com.pksofter.www.homechat.LoginActivity.UpdateToken =
                            com.pksofter.www.homechat.LoginActivity.UpdateToken()
                        updateToken.execute()
                        if (sessionMAnager.getUsertype().equals("admin")) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    ConverSationsActivity::class.java
                                )
                            )
                            finish()
                        }
                        finish()
                    */
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MapViewActivity::class.java
                            )
                        )
                        finish()
                        Toast.makeText(this@LoginActivity, "LoginSucccess", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@LoginActivity, "error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@LoginActivity, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}