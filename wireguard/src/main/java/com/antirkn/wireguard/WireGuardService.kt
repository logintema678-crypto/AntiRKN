package com.antirkn.wireguard

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor

class WireGuardService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Здесь будет логика WireGuard
        return START_STICKY
    }

    override fun onDestroy() {
        vpnInterface?.close()
        super.onDestroy()
    }
}