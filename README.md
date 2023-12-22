# MyUMKM

You only need to set up the Firebase. Here are the steps:

## Firebase Setup
1. Create the project in Firebase and set the name of your project.
2. After that, enable Google Analytics and press continue.
3. Choose account and create project.

## Firestore
1. In the Firestore section, click 'Create Database', select location, start in test mode, and enable it.

## Firebase ML
1. Click 'Machine Learning' in the sidebar and get started.
2. Click on the 'Custom' option, after that deploy your model.
3. Reminder: the model is tflite.

## Firebase Authentication
1. Get started.
2. Enable the email/password and Google Auth.

After these steps, change the `google-services.json` in app/src folder and your app is ready to go.
