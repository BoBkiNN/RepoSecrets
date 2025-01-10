package xyz.bobkinn.repoSecrets.gradle

import xyz.bobkinn.repoSecrets.loadCredProperties
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class RepoSecretsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.loadCredProperties()
    }
}