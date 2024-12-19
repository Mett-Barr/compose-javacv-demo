import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    id("java")

    id("org.openjfx.javafxplugin") version "0.0.14"
    id("org.bytedeco.gradle-javacpp-platform") version "1.5.10"
}

val javacvVersion = "1.5.10"

/**
 * https://github.com/bytedeco/gradle-javacpp
 * 文檔在這，不過用法不確定是哪種，可能要都試試看
 */
ext.set("javacppPlatform", "windows-x86_64,macosx-arm64,macosx-x86_64")

kotlin {
    jvm("desktop") {
        withJava()
    }
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            api("org.bytedeco:javacv-platform:$javacvVersion")
        }
    }
}

javafx {
    version = "22.0.1"
    modules("javafx.controls", "javafx.swing")
}


compose.desktop {
    application {
        mainClass = "com.test.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.test"
            packageVersion = "1.0.0"
        }
    }
}
