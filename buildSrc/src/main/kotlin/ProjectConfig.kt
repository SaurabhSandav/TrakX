@file:Suppress("unused")

object Android {
    private const val GRADLE_PLUGIN_VERSION = "3.6.0-alpha03"

    const val GRADLE_PLUGIN = "com.android.tools.build:gradle:$GRADLE_PLUGIN_VERSION"
}

object Kotlin {
    private const val VERSION = "1.3.41"
    private const val COROUTINES_VERSION = "1.2.2"
    private const val SERIALIZATION_VERSION = "0.11.1"

    const val GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
    const val SERIALIZATION_PLUGIN = "org.jetbrains.kotlin:kotlin-serialization:$VERSION"
    const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$VERSION"
    const val SERIALIZATION_RUNTIME = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$SERIALIZATION_VERSION"
    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
}

object JUnit {
    private const val JUNIT_VERSION = "4.12"

    const val JUNIT = "junit:junit:$JUNIT_VERSION"
}

object AndroidXTest {
    private const val CORE_KTX_VERSION = "1.2.0"
    private const val RUNNER_VERSION = "1.2.0"
    private const val EXT_JUNIT_KTX_VERSION = "1.1.1"

    const val CORE_KTX = "androidx.test:core-ktx:$CORE_KTX_VERSION"
    const val RUNNER = "androidx.test:runner:$RUNNER_VERSION"
    const val EXT_JUNIT_KTX = "androidx.test.ext:junit-ktx:$EXT_JUNIT_KTX_VERSION"
}

object Espresso {
    private const val VERSION = "3.2.0"

    const val CORE = "androidx.test.espresso:espresso-core:$VERSION"
}

object Material {
    private const val VERSION = "1.1.0-alpha09"

    const val MATERIAL = "com.google.android.material:material:$VERSION"
}

object Jetpack {
    private const val ACTIVITY_KTX_VERSION = "1.1.0-alpha02"
    private const val APPCOMPAT_VERSION = "1.1.0-rc01"
    private const val CORE_KTX_VERSION = "1.2.0-alpha03"
    private const val FRAGMENT_KTX_VERSION = "1.2.0-alpha02"
    private const val PREFERENCE_KTX_VERSION = "1.1.0-rc01"
    private const val DRAWERLAYOUT_VERSION = "1.1.0-alpha02"
    private const val RECYCLERVIEW_VERSION = "1.1.0-beta02"
    private const val CONSTRAINT_LAYOUT_VERSION = "2.0.0-beta2"

    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:$ACTIVITY_KTX_VERSION"
    const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
    const val CORE_KTX = "androidx.core:core-ktx:$CORE_KTX_VERSION"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:$FRAGMENT_KTX_VERSION"
    const val PREFERENCE_KTX = "androidx.preference:preference-ktx:$PREFERENCE_KTX_VERSION"
    const val DRAWERLAYOUT = "androidx.drawerlayout:drawerlayout:$DRAWERLAYOUT_VERSION"
    const val RECYCLERVIEW = "androidx.recyclerview:recyclerview:$RECYCLERVIEW_VERSION"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:$CONSTRAINT_LAYOUT_VERSION"
}

object Lifecycle {
    private const val VERSION = "2.2.0-alpha03"

    const val EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:$VERSION"
    const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
    const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    const val COMMON_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$VERSION"
}

object Navigation {
    private const val VERSION = "2.2.0-alpha01"

    const val FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:$VERSION"
    const val UI_KTX = "androidx.navigation:navigation-ui-ktx:$VERSION"
    const val SAFE_ARGS_GRADLE_PLUGIN = "androidx.navigation:navigation-safe-args-gradle-plugin:$VERSION"
}

object Paging {
    private const val VERSION = "2.1.0"

    const val RUNTIME_KTX = "androidx.paging:paging-runtime-ktx:$VERSION"
}

object WorkManager {
    private const val VERSION = "2.2.0-rc01"

    const val RUNTIME_KTX = "androidx.work:work-runtime-ktx:$VERSION"
}

object Dagger {
    private const val VERSION = "2.24"

    const val DAGGER = "com.google.dagger:dagger:$VERSION"
    const val ANDROID_SUPPORT = "com.google.dagger:dagger-android-support:$VERSION"
    const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"
    const val ANDROID_PROCESSOR = "com.google.dagger:dagger-android-processor:$VERSION"
}

object Retrofit {
    private const val VERSION = "2.6.1"
    private const val SERIALIZATION_VERSION = "0.4.0"

    const val RETROFIT = "com.squareup.retrofit2:retrofit:$VERSION"
    const val CONVERTER_MOSHI = "com.squareup.retrofit2:converter-moshi:$VERSION"
    const val CONVERTER_SERIALIZATION =
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$SERIALIZATION_VERSION"
}

object Picasso {
    private const val VERSION = "2.71828"

    const val PICASSO = "com.squareup.picasso:picasso:$VERSION"
}

object OkHttp {
    private const val VERSION = "4.0.1"

    const val OKHTTP = "com.squareup.okhttp3:okhttp:$VERSION"
}

object LeakCanary {
    private const val VERSION = "2.0-beta-2"

    const val ANDROID = "com.squareup.leakcanary:leakcanary-android:$VERSION"
}

object Gander {
    private const val VERSION = "3.1.0"

    const val GANDER = "com.ashokvarma.android:gander:$VERSION"
    const val GANDER_NO_OP = "com.ashokvarma.android:gander-no-op:$VERSION"
    const val GANDER_PERSISTENCE = "com.ashokvarma.android:gander-persistence:$VERSION"
}

object SQLDelight {
    private const val VERSION = "1.1.4"

    const val GRADLE_PLUGIN = "com.squareup.sqldelight:gradle-plugin:$VERSION"
    const val ANDROID_DRIVER = "com.squareup.sqldelight:android-driver:$VERSION"
    const val ANDROID_PAGING_EXTENSIONS = "com.squareup.sqldelight:android-paging-extensions:$VERSION"
    const val COROUTINES_EXTENSIONS = "com.squareup.sqldelight:coroutines-extensions-jvm:$VERSION"
}
