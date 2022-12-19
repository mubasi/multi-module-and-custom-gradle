object Version {
    val minSdk by lazy { 26 }
    val compileSdk by lazy { 32 }
    val targetSdk by lazy { 32 }

    private val major by lazy { 1 }
    private val minor by lazy { 1 }
    private val patch by lazy { 1 }

    private val versionType by lazy { "" }

    val appName by lazy { "VSM Pangkalan" }
    val appNameStaging by lazy { "$appName (Staging)" }
    val appNameDev by lazy { "$appName (Dev)" }

    val versionName by lazy { "$major.$minor.$patch$versionType" }
    val versionCode by lazy { 11 }
}