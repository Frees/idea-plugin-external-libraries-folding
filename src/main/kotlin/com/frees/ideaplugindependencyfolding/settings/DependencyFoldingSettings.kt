package com.frees.ideaplugindependencyfolding.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Persistent settings for the External Lib Folding plugin.
 */
@State(
    name = "ExternalLibFoldingSettings",
    storages = [Storage("externalLibFoldingSettings.xml")]
)
class DependencyFoldingSettings : PersistentStateComponent<DependencyFoldingSettings> {
    // Whether folding is enabled
    var foldingEnabled: Boolean = true

    // Default common package prefixes
    var commonPrefixes: MutableList<PrefixMapping> = mutableListOf(
        PrefixMapping("org.springframework", "Spring"),
        PrefixMapping("org.apache", "Apache"),
        PrefixMapping("com.google", "Google"),
        PrefixMapping("io.netty", "Netty"),
        PrefixMapping("org.jetbrains", "JetBrains"),
        PrefixMapping("com.fasterxml", "FasterXML"),
        PrefixMapping("org.slf4j", "SLF4J"),
        PrefixMapping("org.junit", "JUnit"),
        PrefixMapping("javax", "Java EE"),
        PrefixMapping("jakarta", "Jakarta EE"),
        PrefixMapping("com.squareup", "Square"),
        PrefixMapping("com.github", "GitHub"),
        PrefixMapping("org.hibernate", "Hibernate"),
        PrefixMapping("org.mockito", "Mockito"),
        PrefixMapping("org.eclipse", "Eclipse"),
        PrefixMapping("org.gradle", "Gradle"),
        PrefixMapping("org.scala-lang", "Scala"),
        PrefixMapping("org.apache.commons", "Apache Commons"),
        PrefixMapping("org.bouncycastle", "Bouncy Castle"),
        PrefixMapping("org.mapstruct", "MapStruct"),
        PrefixMapping("org.thymeleaf", "Thymeleaf"),
        PrefixMapping("org.yaml", "YAML"),
        PrefixMapping("org.dom4j", "Dom4j"),
        PrefixMapping("org.hamcrest", "Hamcrest"),
        PrefixMapping("org.glassfish", "GlassFish"),
        PrefixMapping("org.jboss", "JBoss"),
        PrefixMapping("org.springframework.boot", "Spring Boot"),
        PrefixMapping("org.springframework.cloud", "Spring Cloud"),
        PrefixMapping("org.springframework.data", "Spring Data"),
        PrefixMapping("org.springframework.security", "Spring Security"),
        PrefixMapping("org.springframework.web", "Spring Web")
    )

    override fun getState(): DependencyFoldingSettings = this

    override fun loadState(state: DependencyFoldingSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): DependencyFoldingSettings {
            return service()
        }
    }
}

/**
 * Represents a mapping between a package prefix and its display name.
 */
data class PrefixMapping(
    var prefix: String = "",
    var displayName: String = ""
)
