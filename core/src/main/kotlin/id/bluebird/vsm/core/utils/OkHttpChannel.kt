package id.bluebird.vsm.core.utils

import android.util.Log
import id.bluebird.vsm.core.BuildConfig
import id.bluebird.vsm.core.utils.hawk.AuthUtils
import io.grpc.*
import io.grpc.okhttp.OkHttpChannelBuilder
import java.util.concurrent.TimeUnit

class OkHttpChannel {

    companion object {
        private const val DEVICE_IS_NOT_AUTHORIZED = "device not authorized"

        private val AUTHORIZATION_KEY: Metadata.Key<String> =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
        private val DEVICE_ID_KEY: Metadata.Key<String> =
            Metadata.Key.of("device_id", Metadata.ASCII_STRING_MARSHALLER)
        private val VERSION_CODE: Metadata.Key<String> =
            Metadata.Key.of("version_code", Metadata.ASCII_STRING_MARSHALLER)

        var lastRequest: (() -> Unit)? = null

        val TAG = OkHttpChannel::class.java.simpleName

        var channel: ManagedChannel? = null
        var versionCode: Long? = null

        private val loggingInterceptor = object : ClientInterceptor {
            override fun <ReqT, RespT> interceptCall(
                method: MethodDescriptor<ReqT, RespT>,
                callOptions: CallOptions,
                next: Channel
            ): ClientCall<ReqT, RespT> {

                return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                    next.newCall(
                        method,
                        callOptions.withDeadlineAfter(30, TimeUnit.SECONDS)
                    )
                ) {
                    override fun sendMessage(message: ReqT) {
                        Log.d(
                            TAG,
                            method.fullMethodName + "\n ---Request Content---\n" + message.toString()
                        )
                        super.sendMessage(message)
                    }

                    override fun start(responseListener: Listener<RespT>?, headers: Metadata?) {
                        val auth: String = AuthUtils.getAccessToken()
                        initHeader(headers = headers, auth = auth)

                        val listener = object : Listener<RespT>() {
                            override fun onMessage(message: RespT) {
                                Log.d(
                                    TAG,
                                    method.fullMethodName + "\n ---Response Content---\n" + message.toString()
                                )
                                super.onMessage(message)
                                responseListener?.onMessage(message)
                            }

                            override fun onHeaders(headers: Metadata?) {
                                super.onHeaders(headers)
                                responseListener?.onHeaders(headers)
                            }

                            override fun onClose(status: Status?, trailers: Metadata?) {
                                super.onClose(status, trailers)
                                if (isValidToken(status?.description).not() && auth.isNullOrEmpty()
                                        .not()
                                ) {
                                    responseListener?.onClose(status, trailers)
                                    tokenCallback?.onTokenExpired()
                                } else {
                                    responseListener?.onClose(status, trailers)
                                }
                            }

                            override fun onReady() {
                                super.onReady()
                                responseListener?.onReady()
                            }
                        }
                        super.start(listener, headers)
                    }
                }
            }
        }

        fun initHeader(headers: Metadata?, auth : String?){
            headers?.let {
                if (auth.isNullOrEmpty().not()) {
                    it.put(
                        AUTHORIZATION_KEY,
                        "Bearer $auth",
                    )
                }
                it.put(
                    DEVICE_ID_KEY,
                    AuthUtils.getAppUUID(),
                )
                it.put(
                    VERSION_CODE,
                    versionCode.toString(),
                )
            }
        }

        fun initChannel(tokenExpiredCallback: TokenExpiredCallback, vCode: Long?) {
            versionCode = vCode ?: -1
            val builder: ManagedChannel =
                OkHttpChannelBuilder.forAddress(BuildConfig.BASE_URL, 443)
                    .intercept(loggingInterceptor)
                    .enableRetry()
                    .build()
            channel = builder
            tokenCallback = tokenExpiredCallback
            listenConnectionStatus()
        }

        private fun listenConnectionStatus() {
            object : Runnable {
                override fun run() {
                    val currentState = channel?.getState(false)
                    channel?.notifyWhenStateChanged(currentState, this)
                    callback?.onConnectionChanged(currentState)
                }
            }.also {
                it.run()
            }
        }

        var callback: Callback? = null

        interface Callback {

            fun onConnectionChanged(currentState: ConnectivityState?)

        }

        var tokenCallback: TokenExpiredCallback? = null

        interface TokenExpiredCallback {

            fun onTokenExpired()

        }

        private fun isValidToken(description: String?): Boolean {
            if (description == null) {
                return true
            }

            return !(description == "JWT Token was invalid" || description == "JWT Token is expired"
                    || description == "JWT Token is malformed" || description == "token is not valid yet" || description == "JWT is expired"
                    || description == "unexpected signing method" || description == "session not found" || description == DEVICE_IS_NOT_AUTHORIZED)
        }
    }
}
