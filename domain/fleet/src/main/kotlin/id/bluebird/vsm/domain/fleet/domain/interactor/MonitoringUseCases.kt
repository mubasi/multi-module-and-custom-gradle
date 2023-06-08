package id.bluebird.vsm.domain.fleet.domain.interactor

import com.google.firebase.database.*
import id.bluebird.vsm.domain.fleet.MonitoringResultState
import id.bluebird.vsm.domain.fleet.domain.cases.Monitoring
import id.bluebird.vsm.domain.fleet.model.MonitoringData
import id.bluebird.vsm.domain.fleet.model.MonitoringResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MonitoringUseCases : Monitoring {
    companion object {

        private const val MONITORING = "digital-outlet-monitoring"
        private const val STAGING = "staging"
        private const val PROD = "production"
        private const val DEV = "dev"
    }

    private lateinit var _ref: DatabaseReference

    override fun invoke(): Flow<MonitoringResultState> = callbackFlow {
        val baseReference = FirebaseDatabase.getInstance().reference
        initRef(baseReference)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result: List<MonitoringResult> = snapshot.children.map {
                    val value =
                        it.getValue(MonitoringData::class.java) ?: throw NullPointerException()
                    MonitoringResult(
                        buffer = value.buffer,
                        locationName = value.locationName,
                        subLocationName = value.subLocationName,
                        queueFleet = value.queueFleet,
                        queuePassenger = value.queuePassenger,
                        request = value.request,
                        subLocationId = value.subLocationId,
                        totalQueueFleet = value.totalQueueFleet,
                        totalQueuePassenger = value.totalQueuePassenger,
                        totalRitase = value.totalRitase
                    )
                }
                this@callbackFlow.trySendBlocking(MonitoringResultState.Success(result))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(MonitoringResultState.Error(error.toException()))
            }
        }

        _ref.addValueEventListener(listener)
        awaitClose {
            _ref.removeEventListener(listener)
        }
    }

    private fun initRef(baseReference: DatabaseReference) {
        val parent = when (BuildConfig.FLAVOR) {
            "prod" -> PROD
            "stage" -> STAGING
            else -> DEV
        }
        _ref = baseReference.child(parent).child(MONITORING)
    }
}
