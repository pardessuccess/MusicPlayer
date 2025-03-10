plugins {
    alias(libs.plugins.pardess.android.library)
    alias(libs.plugins.pardess.hilt)
}

android {
    namespace = "com.pardess.data"
}

dependencies {

    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:media-query"))
    implementation(project(":core:media-service"))
    implementation(project(":core:domain"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(libs.media3.session)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}