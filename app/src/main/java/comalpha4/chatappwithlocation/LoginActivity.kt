package comalpha4.chatappwithlocation

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.permissionx.guolindev.PermissionX
import comalpha4.chatappwithlocation.Models.UserModel
import comalpha4.chatappwithlocation.services.LocationService

class LoginActivity : AppCompatActivity() {
    var reference: DatabaseReference? = null
    lateinit var signup: TextView
    lateinit var btnLogin: TextView
    lateinit var phoneNumberET: TextInputEditText
    lateinit var passwordEt: TextInputEditText

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
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    fun validation() {
        if (passwordEt.getText().toString().trim { it <= ' ' } == "") {
            passwordEt.setError("enter password")
        } else if (phoneNumberET.getText().toString().trim { it <= ' ' } == "") {
            phoneNumberET.setError("enter email")
        } else {
            checkPermissions()
        }
    }

    lateinit var userModel: UserModel
    fun LoginUser() {
        val query: Query =
            reference!!.orderByChild("email").equalTo(phoneNumberET.getText().toString())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (data in dataSnapshot.children) {
                        userModel = UserModel(
                            data.child("userId").getValue<String>(String::class.java),
                            data.child("email").getValue<String>(String::class.java),
                            data.child("fcm").getValue<String>(String::class.java),
                            data.child("latlng").getValue<String>(String::class.java),
                            data.child("name").getValue<String>(String::class.java),
                            data.child("password").getValue<String>(String::class.java),
                            data.child("phone").getValue<String>(String::class.java)
                        )
                    }
                    if (userModel.password == passwordEt.getText().toString()) {
                        SessionManagement(this@LoginActivity).setUserInfo(userModel)
                        startActivity(Intent(this@LoginActivity, MapViewActivity::class.java))

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

    private fun checkPermissions() {
        PermissionX.init(this)
            .permissions(
                "android.permission.POST_NOTIFICATIONS",
                "android.permission.FOREGROUND_SERVICE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION"
            )
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()

                    LoginUser()
                } else {
                    Toast.makeText(
                        this,
                        "Some permissions are denied: $deniedList",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }



}