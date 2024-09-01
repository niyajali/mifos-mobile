plugins {
    alias(libs.plugins.mifos.android.feature)
    alias(libs.plugins.mifos.android.library.compose)
}

android {
    namespace = "org.mifos.mobile.feature.settings"
}

dependencies {
    implementation(libs.androidx.appcompat)
}