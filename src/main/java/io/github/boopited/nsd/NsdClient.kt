package io.github.boopited.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log

class NsdClient(context: Context, private val serviceType: String, private val callback: Callback)
    : NsdManager.DiscoveryListener, NsdManager.ResolveListener {

    private val nsdManager by lazy { context.getSystemService(Context.NSD_SERVICE) as NsdManager }

    interface Callback {
        fun onDiscoveryStart(success: Boolean, errorCode: Int)
        fun onDiscoveryStop(success: Boolean, errorCode: Int)
        fun onServiceFound(serviceInfo: NsdServiceInfo)
        fun onServiceLost(serviceInfo: NsdServiceInfo)
        fun onServiceResolved(serviceInfo: NsdServiceInfo)
        fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int)
    }

    fun start() {
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, this)
    }

    fun stop() {
        nsdManager.stopServiceDiscovery(this)
    }

    override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
        callback.onDiscoveryStart(false, errorCode)
    }

    override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
        callback.onDiscoveryStop(false, errorCode)
    }

    override fun onDiscoveryStarted(serviceType: String?) {
        callback.onDiscoveryStart(true, 0)
    }

    override fun onDiscoveryStopped(serviceType: String?) {
        callback.onDiscoveryStop(true, 0)
    }

    override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
        serviceInfo?.let {
            when {
                it.serviceType != serviceType ->
                    Log.d(TAG, "Unknown Service Type: ${it.serviceType}")
                else -> {
                    callback.onServiceFound(it)
                    nsdManager.resolveService(it, this)
                }
            }
        }
    }

    override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
        serviceInfo?.let {
            when {
                it.serviceType != serviceType ->
                    Log.d(TAG, "Unknown Service Type: ${it.serviceType}")
                else ->
                    callback.onServiceLost(it)
            }
        }
    }

    override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
        serviceInfo?.let {
            callback.onResolveFailed(it, errorCode)
        }
    }

    override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
        serviceInfo?.let {
            callback.onServiceResolved(it)
        }
    }

    companion object {
        private const val TAG = "NsdClient"
    }
}