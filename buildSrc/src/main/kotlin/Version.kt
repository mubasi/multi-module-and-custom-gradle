object Version {
    private const val major = 2
    private const val minor = 2
    private const val patch = 1

    val minSdk by lazy { 26 }
    val compileSdk by lazy { 32 }
    val targetSdk by lazy { 32 }

    val versionType by lazy { "" }

    val appName by lazy { "VSM Pangkalan" }
    val appNameStaging by lazy { "$appName (Staging)" }
    val appNameDev by lazy { "$appName (Dev)" }

    val versionName by lazy { "1.1.0$versionType" }
    val versionCode by lazy { 9 }
    val appVersion by lazy {"$major.$minor.$patch"}
}