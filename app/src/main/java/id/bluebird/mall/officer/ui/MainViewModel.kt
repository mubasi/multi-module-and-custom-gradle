package id.bluebird.mall.officer.ui

import androidx.lifecycle.ViewModel
import id.bluebird.mall.officer.common.Mqtt
import id.bluebird.mall.officer.utils.AuthUtils
import org.eclipse.paho.client.mqttv3.*

class MainViewModel(private val mqtt: Mqtt) : ViewModel() {

    fun mqttConnect() {
        if (isLogin()) {
            mqtt.connect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // do nothing
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    exception?.printStackTrace()
                }
            }, object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    cause?.printStackTrace()
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // do nothing
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    // do nothing
                }
            })
        }
    }

    fun mqttDisconnect() {
        if (isLogin()) {
            mqtt.disconnect(object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    // do nothing
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    // do nothing
                }
            })
        }
    }

    private fun isLogin() = AuthUtils.getAccessToken().isNotEmpty()
}