package id.bluebird.vsm.domain.passenger.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite


@Suite
@SelectClasses(
    CounterBarCasesTest::class,
    CurrentQueueCasesTest::class,
    DeleteSkippedCasesTest::class,
    GetCurrentQueueCaseTest::class,
    GetQueueReceiptCasesTest::class,
    GetWaitingQueueCasesTest::class,
    ListQueueSkippedCasesTest::class,
    ListQueueWaitingCasesTest::class,
    RestoreSkippedCasesTest::class,
    SearchQueueCasesTest::class,
    SearchWaitingQueueCasesTest::class,
    SkipQueueCasesTest::class,
    TakeQueueCasesTest::class
)
@ExperimentalCoroutinesApi
class RunSuiteDomainPassenger