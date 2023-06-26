object Version {
    val minSdk by lazy { 26 }
    val compileSdk by lazy { 33 }
    val targetSdk by lazy { 33 }

    private val major by lazy { 1 }
    private val minor by lazy { 2 }
    private val patch by lazy { 1 }

    private val versionType by lazy { "" }

    val appName by lazy { "MM Single Gradle" }
    val appNameStaging by lazy { "$appName (Staging)" }
    val appNameDev by lazy { "$appName (Dev)" }
    val versionName by lazy { "$major.$minor.$patch$versionType" }
    val versionCode by lazy { 19 }
}