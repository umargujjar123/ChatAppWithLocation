package comalpha4.chatappwithlocation

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Hashtable

class SignUpActivity : AppCompatActivity() {
    lateinit var name: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var phone: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var LoginBtn: TextView
    var reference: DatabaseReference? = null
    var mtoken:String = ""

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
                    insertDataToFirebase()
                }
            }
        /*FirebaseInstanceId.getInstance().getInstanceId()
             .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                 @Override
                 public void onComplete(@NonNull Task<InstanceIdResult> task) {

                     if (!task.isSuccessful()) {
                         Log.w("error FCM ", "getInstanceId failed", task.getException());
                         return;
                     }

                     // Get new Instance ID token
                     token = task.getResult().getToken();

                     // Log and toast

//                        Toast.makeText(RegisterActivity.this, token, Toast.LENGTH_SHORT).show();
                 }
             });*/return mtoken
    }

    private fun insertDataToFirebase() {
        reference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map: MutableMap<String, Any> = Hashtable()
                val key: String? = reference!!.push().getKey()
                map["userId"] = key!!
                map["name"] = name.getText().toString()
                map["email"] = email.getText().toString()
                map["phone"] = phone.getText().toString()
                map["latlng"] = "n/a"
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
}