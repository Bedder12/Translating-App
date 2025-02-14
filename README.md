# LinguaLink - Android Language Translation App

#Overview

LinguaLink is an Android application that utilizes Firebase ML Kit to provide text translation services. The app supports both text input and voice input (Speech-to-Text) for translations. Users can choose a source and target language, input text for translation, or use their voice for input. The app efficiently communicates with Firebase for natural language processing, ensuring accurate and seamless translation services. Developed in Kotlin, the app follows Android best practices and integrates Material Design for an intuitive and user-friendly interface.

# Features
- Text Translation: Users can input text and translate it from one language to another.
- Voice Input: Uses Speech-to-Text functionality to allow users to speak their translation.
- Multi-language Support: Supports multiple source and target languages.
- Real-Time Feedback: Instant translation results and system feedback on missing inputs.
- Firebase Integration: Leverages Firebase ML Kit for efficient language processing and translation.
- Material Design: Designed with Google’s Material Design for a clean, modern interface

# How to Run the Program
Prerequisites
- Android Studio installed with the latest SDKs.
- Firebase Account: To configure Firebase and integrate it with your app.

Setup and Configuration
1. Clone the Repository: Clone the project repository to your local machine.
- git clone https://github.com/your-username/lingualink.git
2. Firebase Setup:
  - Create a new Firebase project in the Firebase Console.
  - Download the google-services.json file from Firebase and place it in the app folder of your project.
3. Add Firebase Dependencies:
- Open your build.gradle files and ensure Firebase dependencies are added. Include:
-com.google.firebase:firebase-core
-com.google.firebase:firebase-ml-natural-language
-com.google.firebase:firebase-ml-natural-language-translate-model
4. Sync and Build: Once Firebase is configured and dependencies are added, sync the project and build it in Android Studio.
5. Run the Application: Use an emulator or connect a physical Android device and run the app from Android Studio.

# App Workflow
- Language Selection: Users select the source and target languages via dropdown menus (Spinner elements).
- Text Input: The user enters text in the provided input field.
- Voice Input: Users can use the microphone button to dictate text for translation, which is then converted into text via Google’s Speech-to-Text service.
- Translation: Upon clicking the Translate button, the app processes the text using Firebase ML Kit and returns the translated text.  
- Display: The translated text is displayed in a text field.

  
# Example of Use
User Interface
1. Start Screen: Users can choose the languages for translation from dropdown menus.
2. Text Input and Translation: After entering text and selecting languages, users click Translate to get the result.
3. Voice Input: Click on the microphone to enable voice recognition for translation.
4. Translation Result: The translated text is displayed once the process is complete.
   

