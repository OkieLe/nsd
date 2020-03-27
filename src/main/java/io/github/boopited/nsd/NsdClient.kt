package io.github.boopited.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

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

    fun resolve(serviceInfo: NsdServiceInfo) {
        nsdManager.resolveService(serviceInfo, this)
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
            callback.onServiceFound(it)
        }
    }

    override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
        serviceInfo?.let {
            callback.onServiceLost(it)
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
}