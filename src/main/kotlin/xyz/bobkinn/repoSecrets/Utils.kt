package xyz.bobkinn.repoSecrets

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.maven
import java.io.File
import java.net.URI
import java.util.*

fun URI.removeUserInfo(): URI {
    // Rebuild the URI without the user info
    val newUriString = URI(
        scheme,
        null,  // Remove user info
        host,
        port,
        path,
        query,
        fragment
    ).toString()

    return URI(newUriString)
}

fun URI.getAuth(): Pair<String, String?>? {
    return if (userInfo != null) {
        val parts = userInfo.split(":", limit = 2)  // Split only once at the first ':'
        val username = parts[0]
        val password = if (parts.size > 1) parts[1] else null  // Password may not be set
        Pair(username, password)
    } else {
        null  // No user info available
    }
}

@Suppress("unused")
fun RepositoryHandler.credRepository(id: String, properties: MutableMap<String, *>) {
    val url = properties[id+"_URL"] as? String ?: return
    val uri = URI.create(url)
    maven(uri.removeUserInfo()) {
        name = id
        val urlAuth = uri.getAuth()
        val auth = getRepoAuth(id, properties)
        if (auth.first != null || auth.second != null) {
            credentials {
                this.username = auth.first
                this.password = auth.second
            }
        } else if (urlAuth != null) {
            credentials {
                this.username = urlAuth.first
                this.password = urlAuth.second
            }
        }
    }
}

fun getRepoAuth(id: String, properties: MutableMap<String, *>): Pair<String?, String?> {
    val login: String? = properties["${id}_LOGIN"] as? String
    val password: String? = properties["${id}_PASSWORD"] as? String
    return login to password
}

const val DEFAULT_CRED_PROPERTIES = "cred.properties"

fun Project.loadCredProperties(file: String = DEFAULT_CRED_PROPERTIES) {
    val credPropertiesFile = file(file)
    if (credPropertiesFile.exists()) {
        val credProperties = Properties()
        credPropertiesFile.inputStream().use { stream ->
            credProperties.load(stream)
        }
        // Add the properties to the project's properties
        var count = 0
        credProperties.forEach { (key, value) ->
            extensions.extraProperties[key.toString()] = value
            count ++
        }
        if (count > 0) logger.info("Loaded $count properties from cred.properties")
    }
}

@Suppress("unused")
fun PublishingExtension.printPublishResults() {
    val publication = this.publications.named("maven", MavenPublication::class.java).get()
    val ls = publication.artifacts
    println("Published ${ls.size} artifact(s)")
    ls.forEach { artifact ->
        println("- ${artifact.file}")
    }
    val repos = this.repositories
    println("To ${repos.size} repositories:")
    repos.forEach { repo ->
        if (repo is MavenArtifactRepository) {
            val baseUrl = repo.url.toString().let { if (it.endsWith("/")) it else "$it/" }
            val uriText = "$baseUrl${publication.groupId.replace('.', '/')}/${publication.artifactId}/${publication.version}/"
            val uri = URI(uriText)
            if (uri.scheme == "file") {
                val f = File(uri)
                println("- ${repo.name}: $f")
            } else println("- ${repo.name}: $uri")
        }
    }
}