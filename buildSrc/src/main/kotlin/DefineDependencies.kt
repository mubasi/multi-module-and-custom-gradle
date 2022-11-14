object DefineDependencies {

    private val protobuf_artifact : String by lazy {
        "com.google.protobuf:protoc:3.9.1"
    }
    private val gen_artifact : String by lazy {
        "io.grpc:protoc-gen-grpc-java:1.24.2"
    }

    private val javalite_artifact:String by lazy {
        "com.google.protobuf:protoc-gen-javalite:3.0.0"
    }

    fun getProtocArtifact(silicon : String) : String = "$protobuf_artifact$silicon"

    fun getGrpcArtifact(silicon : String) : String = "$gen_artifact$silicon"

    fun getJavaLite(silicon : String) : String = "$javalite_artifact$silicon"

}
