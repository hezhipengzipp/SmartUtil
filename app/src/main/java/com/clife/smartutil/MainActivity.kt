package com.clife.smartutil

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.clife.smartutil.memory.SecondActivity
import com.clife.smartutil.vm.MainViewModel
import dalvik.system.DexClassLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

import java.io.File


class MainActivity : AppCompatActivity() {
    private final val TAG = MainActivity::class.java.simpleName
    private val PERMISSION_REQUEST_CODE: Int = 100
    private val viewModel2: MainViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            100 -> {
                Log.i("fish", "handler")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            // 在 1 秒后执行此代码
        }
        true // 返回 true 表示消息已处理
    }

    companion object {
        val list = mutableListOf<ImageView>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Debug.startMethodTracing("MainActivity")
        var sayException: SayException? = null
        setContentView(R.layout.main_activity)
        val button: Button = findViewById(R.id.bt_say)
        val btAddIv: Button = findViewById(R.id.bt_add_iv)
        val btAddMoery: Button = findViewById(R.id.bt_add_memory)
        btAddMoery.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))

//            Debug.dumpHprofData(externalCacheDir!!.path+File.separator + "dump.hprof")
        }
        btAddIv.setOnClickListener {
            handler.sendEmptyMessageDelayed(100, 3_000)
//            for (i in 0..100) {
//                list.add(ImageView(this))
//            }
//            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.nice)
//            val bundle: Bundle = Bundle()
//            bundle.putBinder("myImager", object : IRmoteService.Stub() {
//                override fun getImage(): Bitmap {
//                    return bitmap
//                }
//            })
//
//            val intent = Intent().apply {
//                setClassName("com.het.sleeppillow", "com.het.sleeppillow.ImageActivity")
//                putExtras(bundle)
////                putExtra("myBinder", bitmap)
//            }
//            startActivity(intent)
        }
        button.setOnClickListener {
            val file =
                File(externalCacheDir!!.path + File.separator + "say_something_hotfix.jar")
            if (file.exists()) {
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
                    EasyPermissions.requestPermissions(
                        this,
                        "需要权限哈哈",
                        PERMISSION_REQUEST_CODE,
                        "android.permission.WRITE_EXTERNAL_STORAGE",
                        "android.permission.READ_EXTERNAL_STORAGE"
                    )
//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf<String>(
//                            "android.permission.WRITE_EXTERNAL_STORAGE",
//                            "android.permission.READ_EXTERNAL_STORAGE"
//                        ),
//                        PERMISSION_REQUEST_CODE
//                    )
                } else {
                    // 已经拥有权限
                    processHotfix()
                }

            }
        }

        Debug.stopMethodTracing()

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

    @AfterPermissionGranted(100)
    fun onPermissionSuccess() {
        Toast.makeText(this, "AfterPermission调用成功了", Toast.LENGTH_SHORT).show()
        processHotfix()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                // 权限被授予
//                processHotfix()
//            } else {
//                // 权限被拒绝
//                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
//            }
//        }

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}

class MemoryLeak {
    fun doSomething() {
        Log.i("MemoryLeak", "doSomething")
    }

    fun createDumpFile() {

    }
}
