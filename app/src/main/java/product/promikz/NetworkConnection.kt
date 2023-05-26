@file:Suppress("DEPRECATION")

package product.promikz

import android.content.Context
import android.net.*
import androidx.lifecycle.LiveData

class NetworkConnection(context: Context) : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback: ConnectivityManager.NetworkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }
        }

    private var isRegistered = false

    override fun onActive() {
        super.onActive()
        updateConnection()
        if (!isRegistered) {
            val requestBuilder = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            connectivityManager.registerNetworkCallback(
                requestBuilder.build(),
                networkCallback
            )
            isRegistered = true
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (isRegistered) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            isRegistered = false
        }
    }

    private fun updateConnection() {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }
}
