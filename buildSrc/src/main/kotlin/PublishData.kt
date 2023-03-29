data class PublishData(val signing: Signing, val artifact: Artifact, val ossrh: OSSRH, val sonatypeStagingProfileId: String)

data class Signing(val keyname: String, val passphrase: String, val executable: String)

data class Artifact(val group: String, val version: String, val id: String)

data class OSSRH(val username: String, val password: String)