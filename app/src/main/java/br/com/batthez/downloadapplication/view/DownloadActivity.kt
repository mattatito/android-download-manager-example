package br.com.batthez.downloadapplication.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.batthez.downloadapplication.R
import com.google.android.material.textfield.TextInputEditText

class DownloadActivity : AppCompatActivity() {

    private val storagePermissionCode = 10000
    private lateinit var downloadButton: Button
    private lateinit var linkEditText: TextInputEditText
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initLayoutWidgets()
    }

    private fun initLayoutWidgets() {
        downloadButton = findViewById(R.id.button_baixar)
        linkEditText = findViewById(R.id.edit_text_link)
        recyclerView = findViewById(R.id.recycler_view_donwloaded_items)
        downloadButton.setOnClickListener {
            downloadButtonOnClick()
        }
    }

    private fun downloadButtonOnClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    storagePermissionCode
                )
            } else {
                verifyLink()
            }
        } else {
            verifyLink()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            storagePermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    verifyLink()
                } else {
                    Toast.makeText(applicationContext, getString(R.string.permissao_negada), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun verifyLink() {
        val inputValue = linkEditText.text.toString()
        if (inputValue.isNotEmpty()) {
            downloadRequest(inputValue)
        }
    }

    private fun downloadRequest(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Título da Notificação")
            .setDescription("Descrição do download")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "${System.currentTimeMillis()}"
            )

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

    }


}