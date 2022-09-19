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
                        locationName = value.location_name,
                        queueFleet = value.queue_fleet,
                        queuePassenger = value.queue_passenger,
                        request = value.request,
                        subLocationId = value.sub_location_id,
                        totalQueueFleet = value.total_queue_fleet,
                        totalQueuePassenger = value.total_queue_passenger,
                        totalRitase = value.total_ritase
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
        _ref = if (BuildConfig.FLAVOR == "stage") {
            baseReference.child(STAGING).child(MONITORING)
        } else {
            baseReference.child(MONITORING)
        }
    }
}