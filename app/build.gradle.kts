plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.andriodpdf"
    compileSdk = 34
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.andriodpdf"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
   //text pdf lib---------------------
   implementation ("com.itextpdf:itextpdf:5.5.13")
   // implementation ("com.itextpdf:itext7-core:7.2.2")
    //text pdf lib---------------------
    //---------glide-------
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.cardview:cardview:1.0.0")
//-----------circularProgressBar---------------------
    implementation ("com.mikhaellopez:circularprogressbar:3.1.0")
    //------------------------
//----------------crop image--------------
    //implementation ("com.github.CanHub:Android-Image-Cropper:4.5.0")
    //----------------crop image--------------

    //implementation("com.vanniktech:android-image-cropper:4.5.0")
   // implementation ("com.theartofdev.edmodo:android-image-cropper:2.7.0")
//------------------------FlexboxLayout
    implementation ("com.google.android.flexbox:flexbox:3.0.0")


}