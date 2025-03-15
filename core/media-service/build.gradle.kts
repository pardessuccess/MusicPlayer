plugins {
    alias(libs.plugins.pardess.android.library)
    alias(libs.plugins.pardess.hilt)
}

android {
    namespace = "com.pardess.media_service"
}

dependencies {

    implementation(project(":core:database"))

    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}