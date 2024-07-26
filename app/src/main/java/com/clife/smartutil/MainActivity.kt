package com.clife.smartutil

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dalvik.system.DexClassLoader
import java.io.File


class MainActivity : AppCompatActivity() {
    private final val TAG = MainActivity::class.java.simpleName
    val PERMISSION_REQUEST_CODE: Int = 1

    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            100 -> {
                Log.i("fish", "handler")
            }
        }
        Log.d("fish", "hello world")
        true // 返回 true 表示消息已处理
    }

    companion object {
        val list = mutableListOf<ImageView>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sayException: SayException? = null
        setContentView(R.layout.main_activity)
        val button: Button = findViewById(R.id.bt_say)
        val btAddIv: Button = findViewById(R.id.bt_add_iv)
        btAddIv.setOnClickListener {
            handler.sendEmptyMessageDelayed(100, 30_000)
            for (i in 0..100) {
                list.add(ImageView(this))
            }
        }
        button.setOnClickListener {
            val file =
                File(externalCacheDir!!.path + File.separator + "say_something_hotfix.jar")
            if (!file.exists()) {
                sayException = SayException()
                Toast.makeText(this, sayException!!.saySomething(), Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "onCreate size: ${file.absolutePath}")
                // 检查并请求权限
                if (ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.WRITE_EXTERNAL_STORAGE"
                    ) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.READ_EXTERNAL_STORAGE"
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf<String>(
                            "android.permission.WRITE_EXTERNAL_STORAGE",
                            "android.permission.READ_EXTERNAL_STORAGE"
                        ),
                        PERMISSION_REQUEST_CODE
                    )
                } else {
                    // 已经拥有权限
                    processHotfix()
                }

            }
        }


    }

    private fun processHotfix() {
        val file =
            File(externalCacheDir!!.path + File.separator + "say_something_hotfix.jar")
        // 读写权限
        val dexClassLoader = DexClassLoader(
            file.absolutePath,
            externalCacheDir!!.absolutePath,
            null,
            classLoader
        )

        try {
            val clazz = dexClassLoader.loadClass("com.clife.smartutil.SayHotFix")
            val say = clazz.newInstance() as ISay
            Toast.makeText(this, say.saySomething(), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予
                processHotfix()
            } else {
                // 权限被拒绝
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class MemoryLeak {
    fun doSomething() {
        Log.i("MemoryLeak", "doSomething")
    }
}
