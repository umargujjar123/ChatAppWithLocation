package comalpha4.chatappwithlocation

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import comalpha4.chatappwithlocation.Models.ChatsModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Hashtable
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    lateinit var messageAdapter: MessageAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var txt_senderId: String
    lateinit var txt_receiverId: String
    lateinit var dateCurrent: String
    lateinit var etMessage: EditText
    lateinit var btnSend: Button
    lateinit var tvnameqqq: TextView
    var chat_list: ArrayList<ChatsModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chat_list = ArrayList<ChatsModel>()
        tvnameqqq = findViewById<TextView>(R.id.tvnameqqq)
        recyclerView = findViewById(R.id.rvChat)
        btnSend = findViewById(R.id.btnSend)
        etMessage = findViewById(R.id.etMessage)
        txt_senderId = SessionManagement(this).getUserId()
        txt_receiverId = intent.getStringExtra("userid")!!
        tvnameqqq.setText(intent.getStringExtra("title"))!!
        readMessage(senderId = txt_senderId, readerId = txt_receiverId)
        btnSend.setOnClickListener { insertDataToFirebase(etMessage.text.toString())
        etMessage.setText("")}

    }

    private fun readMessage(readerId: String, senderId: String) {
        getDate()
        chat_list!!.clear()
        reference = FirebaseDatabase.getInstance().getReference("chats")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chat_list = ArrayList<ChatsModel>()
                if (dataSnapshot.hasChildren()) {
                    for (snapshot in dataSnapshot.children) {


//                    ChatModel chat = snapshot.getValue(ChatModel.class);
//                    assert chat != null;

                        val chat = ChatsModel(
                            snapshot.child("date").getValue(String::class.java),
                            snapshot.child("message").getValue(String::class.java),
                            snapshot.child("receiverid").getValue(String::class.java),
                            snapshot.child("senderid").getValue(String::class.java),
                            snapshot.child("time").getValue(String::class.java)
                        )

                        if (chat.receiverid.equals(readerId) && chat.senderid
                                .equals(senderId) || chat.receiverid
                                .equals(senderId) && chat.senderid.equals(readerId)
                        ) {
                            chat_list!!.add(chat)
                        }

                    }
                    messageAdapter = MessageAdapter(this@ChatActivity, chat_list)
                    recyclerView.setLayoutManager(LinearLayoutManager(this@ChatActivity))
                    recyclerView.setAdapter(messageAdapter)
                    messageAdapter.notifyDataSetChanged()
                    //                rv_msg.smoothScrollToPosition(chat_list.size()-1);
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter()!!.getItemCount())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.i("DB Error in readMessage", "onCancelled: Error -> $databaseError")
            }
        })
    }

    var reference: DatabaseReference? = null
    fun getDate(): String? {
        val timeReal: String
        val date = Date()
        val datee = date.toString().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        dateCurrent = datee[0] + " " + datee[1] + " " + datee[2]
        val dateFormatWithZone = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateFormatWithZone2 = SimpleDateFormat("HH:mm", Locale.getDefault())
        val temp =
            dateFormatWithZone2.format(date).split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val t = temp[0].toInt()
        timeReal = if (t > 12) {
            (t - 12).toString() + ":" + temp[1] + " pm"
        } else {
            dateFormatWithZone2.format(date) + " am"
        }

        return timeReal
    }

    private fun insertDataToFirebase( etmsg:String) {
        reference = FirebaseDatabase.getInstance().getReference("chats")
        reference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val map: MutableMap<String, Any> = Hashtable()
                val key: String = reference!!.push().getKey()!!
                reference!!.child(key).setValue(map)
                map["senderid"] = txt_senderId
                map["receiverid"] = txt_receiverId
                map["message"] = etmsg
                map["time"] = getDate().toString()
                map["date"] = dateCurrent
                reference!!.child(key).setValue(map)

//                CreateChat();
                val chatReferenceForSender =
                    FirebaseDatabase.getInstance().getReference("conversations")
                        .child(SessionManagement(this@ChatActivity).getUserId())
                        .child(txt_receiverId)
                chatReferenceForSender.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            val map: MutableMap<String, Any?> = HashMap()
                            map["id"] = txt_receiverId
                            map["lastmsg"] =etmsg
                            map["time"] = getDate().toString()
                            chatReferenceForSender.setValue(map)

                            getFcm()
                        } else {
                            val map: MutableMap<String, Any> = HashMap()
                            map["lastmsg"] = etmsg
                            map["time"] = getDate().toString()
                            chatReferenceForSender.updateChildren(
                                map
                            ) { databaseError, databaseReference ->
                                Log.d("database", "" + databaseError)

                                getFcm()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
                val chatReferenceForReceiver =
                    FirebaseDatabase.getInstance().getReference("conversations")
                        .child(txt_receiverId)
                        .child(SessionManagement(this@ChatActivity).getUserId())
                chatReferenceForReceiver.addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            val map: MutableMap<String, Any> = HashMap()
                            map["id"] = SessionManagement(this@ChatActivity).getUserId()
                            map["lastmsg"] = etmsg
                            map["time"] = getDate().toString()
                            map["name"] = SessionManagement(this@ChatActivity).getUserId()
                            chatReferenceForReceiver.setValue(map)

                            getFcm()
                        } else {
                            val map: MutableMap<String, Any> = HashMap()
                            map["lastmsg"] =etmsg
                            map["time"] = getDate().toString()
                            chatReferenceForReceiver.updateChildren(
                                map
                            ) { databaseError, databaseReference ->
                                Log.d("database", "" + databaseError)

                                getFcm()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ChatActivity, databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    var fcm: String? = null
    var msgText: String? = null
    fun getFcm() {

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("users")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {
                        if (dataSnapshot.child(txt_senderId).exists() && dataSnapshot.child(
                                txt_receiverId
                            ).exists()
                        ) {
                            if (data.child("userId")
                                    .getValue<String>(String::class.java) == txt_receiverId
                            ) {
                                fcm = data.child("fcmToken").getValue<String>(String::class.java)
                            }
                        }
                        //
                    }
//                    sendNotification(
//                        SessionManagement(this@ChatActivity).getUserId(), msgText, getDate(), txt_senderId, fcm
//                    )
                } else {
                    Toast.makeText(this@ChatActivity, "Invalid user", Toast.LENGTH_SHORT).show()
                    //                    new GlideToast.makeToast(ChatingActivity.this, "Invalid user ", GlideToast.LENGTHLONG, GlideToast.CUSTOMTOAST, GlideToast.BOTTOM, R.drawable.ic_close, "#FFAB00").show();
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ChatActivity, databaseError.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    /* fun sendNotification(title: String, msg: String, time: String, senderid: String, fcm: String) {
         val sessionMAnager = SessionMAnager(this@ChatingActivity)
         val gson = Gson()
         val notificationModel = MessageNotificationModel()
         notificationModel.notification.title = title
         notificationModel.notification.body = msg
         notificationModel.notification.time = time
         notificationModel.notification.senderid = senderid
         notificationModel.notification.fromm = "chat"
         //        notificationModel.notification.messageKey = messageKey;
 //        notificationModel.notification.senderUserId = mCurrentUserId;
 //        notificationModel.notification.receiverUserId = mChatUserId;
         notificationModel.data.title = title
         notificationModel.data.body = msg
         notificationModel.data.time = time
         notificationModel.data.senderid = senderid
         notificationModel.data.fromm = "chat"

 //        notificationModel.data.
 //        notificationModel.data.messageKey = messageKey;
 //        notificationModel.data.senderUserId = mCurrentUserId;
 //        notificationModel.data.receiverUserId = mChatUserId;

 //        notificationModel.to = fcm;//ye kia kiya he
         notificationModel.to = fcm


 //       //same par send nai huta ? jis device say karha hun usii par receive
         //dubara bhaij notification
         //token update kr register k doran// new user bnaon ?//jo existing he usi ko update kr dyusko update abi nai kar sakta new bnata hun
         // ya phir har bar login par token update karwaen ?hn thek h
         //challo abi tu new banata hun
         //hugya new login new token
         //bhaij notification
         val requestBody: RequestBody = RequestBody.create(
             MediaType.parse("application/json; charset=utf8"),
             gson.toJson(notificationModel)
         )
         val request: Request = Builder()
             .header("Content-Type", "application/json")
             .addHeader(
                 "Authorization",
                 "key=AAAAYPTAS_U:APA91bG4j733CWJwvEagn6x8RlL18yBb6YlSt9eiXCXJQlWxJZHnRGCpc-9lFLmsNJic_gaEA1_S3QPtC6NWNewn4W5HSNNWkau70fCyaMiA8NGSKUIZl5jruAmiEky87vgfr6uLiHtf"
             )
             .url("https://fcm.googleapis.com/fcm/send")
             .post(requestBody)
             .build()
         val okHttpClient = OkHttpClient()
         okHttpClient.newCall(request).enqueue(object : Callback() {
             fun onFailure(call: Call?, e: IOException) {
                 Log.d("okhttp fail", e.message!!)
                 //                neechy response me kia ata he
 //            log me
 //                yaha oy log me kia ata he?
             } //lol ata hy

             //            check kr k bta  error": "NotRegistered" ye ata hy//is ki pain ko ya
             //            android sy dekha kia ata he okay ruk
             @Throws(IOException::class)
             fun onResponse(call: Call?, response: Response) {
                 Log.d("okhttp response", response.toString())
             }
         })
     }*/
}