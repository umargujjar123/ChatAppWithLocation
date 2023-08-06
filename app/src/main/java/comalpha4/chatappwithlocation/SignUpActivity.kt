package comalpha4.chatappwithlocation

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.permissionx.guolindev.PermissionX
import java.util.Hashtable

class SignUpActivity : AppCompatActivity() {
    lateinit var name: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var LoginBtn: TextView
    var reference: DatabaseReference? = null
    var mtoken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        init()
    }

    fun init() {
        name = findViewById<TextInputEditText>(R.id.NameET)
        email = findViewById<TextInputEditText>(R.id.emailET)
        phone = findViewById<TextInputEditText>(R.id.phoneNumberET)
        password = findViewById<TextInputEditText>(R.id.passwordEt)
        LoginBtn = findViewById<TextView>(R.id.btn_signup)
        reference = FirebaseDatabase.getInstance().getReference("users")

        LoginBtn.setOnClickListener {
            getToken()
        }

    }


    fun getToken(): String? {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mtoken = task.result
                  checkPermissions()
                }
            }
      return mtoken
    }

    private fun insertDataToFirebase(latlng:String) {
        reference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map: MutableMap<String, Any> = Hashtable()
                val key: String? = reference!!.push().getKey()
                map["userId"] = key!!
                map["name"] = name.getText().toString()
                map["email"] = email.getText().toString()
                map["phone"] = phone.getText().toString()
                map["latlng"] = latlng
                map["password"] = password.getText().toString()
                map["fcm"] = mtoken

                reference?.child(key)?.setValue(map)
                finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                /*kProgressHUD.dismiss();*/
                Toast.makeText(this@SignUpActivity, databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }




    private fun checkPermissions() {
        PermissionX.init(this)
            .permissions("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION")
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                    insertDataToFirebase(LocationHelper(this).getCurrentLocation()!!)
                } else {
                    Toast.makeText(this, "Location permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }

}