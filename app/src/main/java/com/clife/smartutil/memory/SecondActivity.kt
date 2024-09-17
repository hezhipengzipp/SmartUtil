package com.clife.smartutil.memory

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Choreographer
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.clife.smartutil.R
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SecondActivity : AppCompatActivity(), NetCallback {
    private var number = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Looper.getMainLooper().setMessageLogging {
//            Log.i("SecondActivityLooper", it)
        }
//        LayoutInflaterCompat.setFactory2(layoutInflater, object : LayoutInflater.Factory2 {
//            override fun onCreateView(
//                parent: View?,
//                name: String,
//                context: Context,
//                attrs: AttributeSet
//            ): View? {
//                val startTime = System.currentTimeMillis()
//                val createView = delegate.createView(parent, name, context, attrs)
//                val endTime = System.currentTimeMillis()
//                println("onCreateView time: $createView=>${endTime - startTime}")
//                return createView
//            }
//
//            override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//                return null
//            }
//        })
//
//        super.onCreate(savedInstanceState)
//        AsyncLayoutInflater(this).inflate(R.layout.activity_second, null) { view, _, _ ->
//            setContentView(view)
//        }
        val data = MutableLiveData<String>()
        setContentView(R.layout.activity_second)
        NetCallBackManager.addCallback(this)
        findViewById<Button>(R.id.bt_open_fps).setOnClickListener {
//            FpsCounter.start()
//            Toast.makeText(this, "number hello nice= $number", Toast.LENGTH_SHORT).show()
//            number++
            val intent = Intent().apply {
                component = ComponentName("com.het.family.sport.controller",
                    "com.het.family.sport.controller.ui.flutter.FamilyMemberActivity")
            }
            startActivity(intent)
        }
        findViewById<Button>(R.id.bt_close_fps).setOnClickListener {
            FpsCounter.stop()
        }

        val create = Retrofit.Builder().baseUrl("https://www.baidu.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetApi::class.java)
        Log.i("SecondActivity", "onCreate: ${create.javaClass}")
        Log.i("SecondActivity", "onCreate->taskId: ${taskId}")
//        create.doneSth().enqueue(object : Callback<String> {
//            override fun onResponse(p0: Call<String>, p1: Response<String>) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onFailure(p0: Call<String>, p1: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })

    }

    override fun doneSth() {
        Choreographer.getInstance().postFrameCallback {
            Log.i("Choreographer", "doneSth: $it")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetCallBackManager.removeCallback(this)
    }
}