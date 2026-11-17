
# Firebase Setup and Demonstration Guide — Morning Verse Application

This guide provides a comprehensive, step-by-step procedure for setting up Firebase Authentication and Cloud Firestore in the **Morning Verse** Android devotional application, along with instructions for testing and demonstrating all features to your instructor.

---

## STEP 1 — Firebase Initial Setup

1. **Open Firebase Console**: Go to [https://console.firebase.google.com/](https://console.firebase.google.com/).
2. **Create a Firebase Project**:
   - Click **Add project** (or **Create a project**).
   - Enter your project name (e.g., `MorningVerseApp`).
   - Choose whether to enable Google Analytics (optional) and click **Create Project**.
3. **Understand the Purpose**: The Firebase project acts as your cloud backend, providing secure authentication services and a NoSQL document database (Cloud Firestore) to store user profiles and personal devotional reflections separately for each user.

---

## STEP 2 — Preconnection / Firebase Console

### 1. Register the Android Application
- In your Firebase project dashboard, click the **Android** icon (`android`) to add an Android app.
- **Android package name**: Enter `com.example.myapplication` (matching `app/build.gradle.kts`).
- App nickname (optional): `Morning Verse App`.
- Click **Register app**.

### 2. Download Configuration File
- Download the `google-services.json` configuration file provided by Firebase.
- Place `google-services.json` inside your app module directory:
  `C:/Users/USER/MyApplication2/app/google-services.json`

### 3. Enable Authentication
- In the Firebase Console sidebar, navigate to **Build** -> **Authentication**.
- Click **Get started**.
- Select the **Email/Password** sign-in provider, toggle **Enable**, and click **Save**.

### 4. Create Cloud Firestore Database
- In the Firebase Console sidebar, navigate to **Build** -> **Cloud Firestore**.
- Click **Create database**.
- Choose a database location close to your users.
- Start in **Test mode** (or Production mode with proper security rules). Click **Enable**.

---

## STEP 3 — Dependencies and Gradle

### 1. Project-Level Gradle (`build.gradle.kts`)
Declare the Google Services Gradle plugin dependency:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}
```

### 2. App-Level Gradle (`app/build.gradle.kts`)
Apply the Google Services plugin and add Firebase BoM, Authentication, and Cloud Firestore dependencies:
```kotlin
plugins {
    alias(libs.plugins.android.application)
}

// Conditionally apply google-services if google-services.json exists
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

dependencies {
    // Firebase BoM and dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}
```

### 3. Sync Gradle
Click **Sync Now** in Android Studio to download and link all required Firebase libraries.

---

## STEP 3.1 — Login and Registration

### 1. UI Layouts & Design
- **Login Screen (`activity_login.xml`)**: Features warm cream (`#F5E8C9`) and soft mint green (`#A6E3BC`) gradients, app logo (`logo3`), Email input, Password input with visibility toggle, a prominent Main Green (`#0BAA63`) login button with rounded pill corners, loading indicator, and a registration link.
- **Registration Screen (`activity_register.xml`)**: Features Full Name, Email, Password, Confirm Password inputs, Register button, and login navigation link.

### 2. Authentication Code Highlights (`LoginActivity.java` & `RegisterActivity.java`)
- **Initialization**: `mAuth = FirebaseAuth.getInstance();`
- **Registration (`createUserWithEmailAndPassword`)**: Validates input fields, creates the account, and automatically saves user profile details in Firestore under `users/{uid}`.
- **Login (`signInWithEmailAndPassword`)**: Authenticates users with email and password, navigating to `MainActivity` upon success.
- **Session Persistence**: Checks `mAuth.getCurrentUser()` when the app launches in `LoginActivity`. If a user is already logged in, the app bypasses login and opens `MainActivity` directly.
- **Logout**: Clears session with `mAuth.signOut()`, clears backstack (`FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK`), and returns securely to `LoginActivity`.

---

## STEP 4 — Firestore Database Structure

### 1. User Profile Collection
- **Collection**: `users`
- **Document ID**: User UID (from Firebase Auth)
- **Fields**:
  - `uid`: String
  - `name`: String
  - `email`: String
  - `createdAt`: Timestamp (Long)

### 2. My Reflections Subcollection
Each user has their own secure subcollection of personal reflections:
- **Path**: `users/{uid}/reflections/{reflectionId}`
- **Fields**:
  - `reflectionId`: String
  - `title`: String
  - `bibleVerse`: String
  - `reflectionNotes`: String
  - `createdAt`: Long
  - `updatedAt`: Long

---

## STEP 4.1 — Firestore CRUD Operations

Implemented in `ReflectionsActivity.java`:

### 1. Create — Adding a Reflection
```kotlin
String reflectionId = reflectionsRef.document().getId();
Map<String, Object> map = new HashMap<>();
map.put("reflectionId", reflectionId);
map.put("title", title);
map.put("bibleVerse", verse);
map.put("reflectionNotes", notes);
map.put("createdAt", System.currentTimeMillis());

reflectionsRef.document(reflectionId).set(map);
```

### 2. Read — Real-time Firestore Listener
```kotlin
reflectionsRef.orderBy("createdAt", Query.Direction.DESCENDING)
    .addSnapshotListener((value, error) -> {
        // Automatically updates RecyclerView when documents change
    });
```

### 3. Update — Modifying an Existing Reflection
```kotlin
Map<String, Object> map = new HashMap<>();
map.put("title", title);
map.put("bibleVerse", verse);
map.put("reflectionNotes", notes);
map.put("updatedAt", System.currentTimeMillis());

reflectionsRef.document(reflectionId).update(map);
```

### 4. Delete — Removing a Reflection with Confirmation
```kotlin
reflectionsRef.document(reflection.getReflectionId()).delete();
```

---

## STEP 5 — Firebase Dashboard Demonstration

To demonstrate your working application to your instructor:

1. **Authentication Verification**:
   - Open Firebase Console -> **Authentication** -> **Users tab**.
   - Show newly registered accounts and their unique UIDs.
2. **Firestore User Profiles**:
   - Open Firebase Console -> **Cloud Firestore** -> **`users`** collection.
   - Show document matching user UID containing name, email, and timestamp.
3. **Reflections Subcollection**:
   - In Firestore, expand a user document -> **`reflections`** subcollection -> document ID.
   - Show fields (`title`, `bibleVerse`, `reflectionNotes`, etc.).
4. **App CRUD Demo**:
   - Tap the **Me** tab / profile icon in the app.
   - Tap **+** to **Create** a reflection. Observe real-time appearance in the app and Firebase Console.
   - Tap the Edit button to **Update** the reflection.
   - Tap the Delete button and confirm to **Delete** the reflection.
5. **Logout & Session Persistence**:
   - Tap **Logout** in the Me section. Verify redirection to Login screen and that pressing Back does not return to dashboard.
   - Log back in and verify that the session persists on app restart.
