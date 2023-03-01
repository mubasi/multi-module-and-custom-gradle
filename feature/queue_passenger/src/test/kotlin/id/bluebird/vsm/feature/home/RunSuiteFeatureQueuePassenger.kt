package id.bluebird.vsm.feature.home

import id.bluebird.vsm.feature.home.dialog_delete_skipped.DialogDeleteSkippedViewModelTest
import id.bluebird.vsm.feature.home.dialog_queue_receipt.DialogQueueReceiptViewModelTest
import id.bluebird.vsm.feature.home.dialog_record_ritase.DialogRecordRitaseViewModelTest
import id.bluebird.vsm.feature.home.dialog_restore_skipped.DialogRestoreSkippedViewModelTest
import id.bluebird.vsm.feature.home.dialog_skip_queue.DialogSkipQueueViewModelTest
import id.bluebird.vsm.feature.home.main.QueuePassengerViewModelTest
import id.bluebird.vsm.feature.home.qr_code.QrCodeViewModelTest
import id.bluebird.vsm.feature.home.queue_search.QueueSearchViewModelTest
import id.bluebird.vsm.feature.home.queue_ticket.QueueTicketViewModelTest
import id.bluebird.vsm.feature.home.ritase_fleet.RitaseFleetViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite

@Suite
@SelectClasses(
    DialogDeleteSkippedViewModelTest::class,
    DialogQueueReceiptViewModelTest::class,
    DialogRestoreSkippedViewModelTest::class,
    DialogSkipQueueViewModelTest::class,
    QueuePassengerViewModelTest::class,
    QueueSearchViewModelTest::class,
    QueueTicketViewModelTest::class,
    RitaseFleetViewModelTest::class,
    DialogRecordRitaseViewModelTest::class,
    QrCodeViewModelTest::class
)

@ExperimentalCoroutinesApi
class RunSuiteFeatureQueuePassenger