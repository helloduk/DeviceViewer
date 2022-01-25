import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.solluzfa.solluzviewer.Log

class AppLifecycle : Application.ActivityLifecycleCallbacks {
    companion object {
        private var TAG = AppLifecycle::class.java.simpleName
    }

    enum class AppStatus {
        BACKGROUND, RETURN_TO_FOREGROUND, FOREGROUND
    }

    var appStatus = AppStatus.BACKGROUND
    private var running = 0

    val isShowing: Boolean
        get() = appStatus != AppStatus.BACKGROUND

    override fun onActivityPaused(activity: Activity?) {
        Log.i(TAG, "onActivityPaused: $activity")
    }

    override fun onActivityResumed(activity: Activity?) {
        Log.i(TAG, "onActivityResumed: $activity")
    }

    override fun onActivityStarted(activity: Activity?) {
        Log.i(TAG, "onActivityStarted: $activity, running($running)")
        if (++running == 1) {
            appStatus = AppStatus.RETURN_TO_FOREGROUND
        } else if (running > 1) {
            appStatus = AppStatus.FOREGROUND
        }
    }

    override fun onActivityDestroyed(activity: Activity?) {
        Log.i(TAG, "onActivityDestroyed: $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
        Log.i(TAG, "onActivitySaveInstanceState: $activity")
    }

    override fun onActivityStopped(activity: Activity?) {
        Log.i(TAG, "onActivityStopped: $activity, running($running)")
        if (--running == 0) {
            appStatus = AppStatus.BACKGROUND
        }
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
        Log.i(TAG, "onActivityCreated: $p0")
    }
}