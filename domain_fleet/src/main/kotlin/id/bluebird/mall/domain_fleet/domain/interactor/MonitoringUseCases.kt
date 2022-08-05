package id.bluebird.mall.domain_fleet.domain.interactor

import com.google.firebase.database.*
import id.bluebird.mall.domain_fleet.MonitoringResultState
import id.bluebird.mall.domain_fleet.domain.cases.Monitoring
import id.bluebird.mall.domain_fleet.model.MonitoringData
import id.bluebird.mall.domain_fleet.model.MonitoringResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MonitoringUseCases: Monitoring {
    companion object {
        private const val MONITORING = "digital-outlet-monitoring"
    }

    private lateinit var _ref: DatabaseReference
    override fun invoke(): Flow<MonitoringResultState> = callbackFlow {
        val baseReference = FirebaseDatabase.getInstance().reference
        _ref = baseReference.child(MONITORING)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result: List<MonitoringResult> = snapshot.children.map {
                    val value = it.getValue(MonitoringData::class.java) ?: throw NullPointerException()
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
}