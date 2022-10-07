import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.thomaskuenneth.clip4moni"
version = "1.4.2"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

val macExtraPlistKeys: String
    get() = """
        <key>LSUIElement</key>
        <string>1</string>
    """.trim()

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.instrument", "java.prefs", "java.scripting", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Clip4Moni"
            packageVersion = "1.4.2"
            description = "Manage text snippets"
            copyright = "2008 - 2022 Thomas Kuenneth. All rights reserved."
            vendor = "Thomas Kuenneth"
            macOS {
                bundleID = "com.thomaskuenneth.clip4moni"
                infoPlist {
                    extraKeysRawXml = macExtraPlistKeys
                }
                iconFile.set(project.file("artwork/Clip4Moni.icns"))
            }
            windows {
                iconFile.set(project.file("artwork/Clip4Moni.ico"))
                menuGroup = "Thomas Kuenneth"
            }
        }
    }
}
