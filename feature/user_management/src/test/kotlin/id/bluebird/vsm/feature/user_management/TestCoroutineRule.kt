package id.bluebird.vsm.feature.user_management

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext

@ExperimentalCoroutinesApi
class TestCoroutineRule : Extension, BeforeEachCallback, AfterEachCallback {

    val testCoroutineDispatcher = TestCoroutineDispatcher()

    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(testCoroutineDispatcher)
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        testCoroutineDispatcher.cleanupTestCoroutines()
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}