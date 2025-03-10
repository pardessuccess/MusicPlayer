plugins {
    alias(libs.plugins.pardess.android.library.compose)
    alias(libs.plugins.pardess.android.library)
}

android {
    namespace = "com.pardess.common"
}

dependencies {

    implementation(project(":core:model"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}