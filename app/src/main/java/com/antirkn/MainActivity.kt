package com.antirkn

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var switchButton: Button
    private lateinit var connectImage: ImageView
    private lateinit var prefs: SharedPreferences

    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("AntiRKN", MODE_PRIVATE)

        statusText = findViewById(R.id.statusText)
        switchButton = findViewById(R.id.switchButton)
        connectImage = findViewById(R.id.connectImage)

        updateSwitchButton()

        // Кнопка "Подключить" (картинка)
        connectImage.setOnClickListener {
            if (isConnected) {
                // Отключаем
                connectImage.setImageResource(R.drawable.ic_connect_off)
                statusText.text = "Не подключено"
                statusText.setTextColor(getColor(R.color.status_red))
                isConnected = false
            } else {
                // Подключаем
                statusText.text = "Подключение..."
                statusText.setTextColor(getColor(R.color.status_yellow))
                
                CoroutineScope(Dispatchers.Main).launch {
                    val bestServer = findBestServer()
                    if (bestServer != null) {
                        connectImage.setImageResource(R.drawable.ic_connect_on)
                        statusText.text = "Подключено"
                        statusText.setTextColor(getColor(R.color.status_green))
                        isConnected = true
                    } else {
                        statusText.text = "Не удалось. Попробуй обход"
                        statusText.setTextColor(getColor(R.color.status_red))
                    }
                }
            }
        }

        // Кнопка "Переключить"
        switchButton.setOnClickListener {
            val current = prefs.getString("mode", "wireguard")
            val newMode = if (current == "wireguard") "byedpi" else "wireguard"
            prefs.edit().putString("mode", newMode).apply()
            updateSwitchButton()
            statusText.text = "Режим: ${if (newMode == "wireguard") "WireGuard" else "ByeDPI"}"
        }
    }

    private fun updateSwitchButton() {
        val mode = prefs.getString("mode", "wireguard")
        switchButton.text = if (mode == "wireguard") "WireGuard" else "ByeDPI"
    }

    private suspend fun findBestServer(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://raw.githubusercontent.com/gfpcom/free-proxy-list/main/lists/wireguard.txt"
                val configs = java.net.URL(url).readText().lines()
                    .filter { it.isNotBlank() && it.startsWith("wireguard://") }
                
                if (configs.isEmpty()) return@withContext null
                configs.firstOrNull()
            } catch (e: Exception) {
                null
            }
        }
    }
}