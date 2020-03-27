package io.github.boopited.nsd

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo

// Service name and type
// https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.xml

class NsdServer(
    context: Context, name: String, type: String,
    private val callback: Callback
) : NsdManager.RegistrationListener {

    interface Callback {
        fun onRegistered(serviceInfo: NsdServiceInfo, errorCode: Int)
        fun onUnregistered(serviceInfo: NsdServiceInfo, errorCode: Int)
    }

    private val nsdManager by lazy { context.getSystemService(Context.NSD_SERVICE) as NsdManager }

    private val serviceInfo = NsdServiceInfo().apply {
        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceName = name
        serviceType = type
    }
    private var serviceName = name

    fun start(port: Int) {
        serviceInfo.port = port
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, this)
    }

    fun stop() {
        nsdManager.unregisterService(this)
    }

    override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
        // Save the service name. Android may have changed it in order to
        // resolve a conflict, so update the name you initially requested
        // with the name Android actually used.
        serviceName = serviceInfo.serviceName
        callback.onRegistered(serviceInfo, 0)
    }

    override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        // Registration failed! Put debugging code here to determine why.
        callback.onRegistered(serviceInfo, errorCode)
    }

    override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
        // Service has been unregistered. This only happens when you call
        // NsdManager.unregisterService() and pass in this listener.
        callback.onUnregistered(serviceInfo, 0)
    }

    override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        // Unregistration failed. Put debugging code here to determine why.
        callback.onUnregistered(serviceInfo, errorCode)
    }
}