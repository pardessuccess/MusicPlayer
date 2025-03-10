plugins {
    alias(libs.plugins.pardess.android.library.compose)
    alias(libs.plugins.pardess.android.feature)
    alias(libs.plugins.pardess.android.room)
}

android {
    namespace = "com.pardess.songs"
}

dependencies {
    implementation(project(":feature:playback"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}