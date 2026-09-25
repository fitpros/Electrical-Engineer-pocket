# Electrical Engineer Pocket

Electrical Engineer Pocket is a native Android engineering toolkit built with Kotlin and Jetpack Compose for electrical engineers, technicians, supervisors, maintenance teams, and students.

## Current modules

- Dashboard
- Electrical calculators
- Equipment and asset records
- Transformer, protection, cable, motor, and solar sections
- PM and PdM maintenance
- Fault analysis
- Inspections and test records
- SCADA telemetry UI
- Protection coordination UI
- Reports and certificates
- User dashboard
- About Developer page
- Admin panel
- Local Room database
- Dark and light themes

## Production-readiness status

This repository is under active development. The current production-readiness branch removes unsafe demo authentication behavior and prepares the project for Firebase integration.

### Important

Do not publish a release build until all items below are completed and verified.

## Authentication architecture

Recommended production architecture:

Android App
-> Firebase Authentication
-> Firestore
-> Firebase Storage
-> Secure backend or Cloud Function
-> Google Sheets API for optional admin reporting

Google Sheets must not be used as the primary user database and must never contain user passwords.

### Firebase setup required

1. Create a Firebase project.
2. Add Android app package:
   `com.aistudio.elecengpro.kzpwe`
3. Download `google-services.json`.
4. Keep `google-services.json` out of public commits when appropriate for your deployment process.
5. Enable Firebase Authentication providers:
   - Email and password
   - Google
6. Configure SHA-1 and SHA-256 signing certificate fingerprints.
7. Create Firestore.
8. Create Firebase Storage if profile photos or user files are required.
9. Configure App Check for production.
10. Add backend security rules before storing production data.

Firebase Auth, Credential Manager, Google ID, and Firestore dependencies are included in the production-readiness branch, but real sign-in remains disabled until the Firebase project is configured.

## Administrator security

A distributable APK must never contain:

- hardcoded administrator passwords
- auto-login admin accounts
- client-side-only admin authority
- privileged secrets

Admin accounts should be provisioned through the trusted backend. Prefer Firebase custom claims for the platform administrator role.

## User data ownership

Before moving engineering records to Firestore, add an ownership model to all user-created records.

Recommended fields:

- `ownerUserId`
- optional `organizationId`
- `createdAt`
- `updatedAt`

Examples:

- equipment
- maintenance records
- faults
- inspections
- reports
- saved calculations
- documents

Firestore security rules must verify the authenticated user's UID on the server side.

## Google Sheets synchronization

Google Sheets is intended only as an optional private admin reporting destination.

Recommended columns:

- User ID
- Full Name
- Email
- Phone
- Country
- City
- Profession
- Company
- Job Title
- Registration Date
- Email Verified
- Account Status
- Registration Source
- Last Login

The Android app must not contain a Google Sheets service-account private key.

Use a secure backend or Cloud Function to perform Sheets writes.

Required behavior:

- registration must succeed even when Sheets is unavailable
- duplicate protection by User ID
- synchronization status must be Pending until the remote write succeeds
- failed jobs must be retryable
- passwords and authentication tokens must never be sent to Sheets

## About Developer

The app includes an About Developer page for Muhammad Imran, Electrical and IT Engineer.

Only verified owner information should be published.

Do not invent or publish:

- email addresses
- phone numbers
- LinkedIn URLs
- websites
- employer names
- job titles

unless the owner has explicitly confirmed them.

## Reports and exports

The current report UI includes report preview screens.

Before public release verify that these are real functions:

- PDF generation
- PDF save and share
- Android print workflow
- Excel or XLSX export
- file naming
- storage permission behavior where applicable

Do not show a success message unless a file was actually generated successfully.

## Privacy and Play Store requirements

Before Google Play publication add and test:

- Privacy Policy
- Terms and Conditions
- Account deletion flow
- Data export flow
- Contact and support
- Firebase data disclosure
- Google Sign-In disclosure where applicable
- user-data deletion from Firestore and synchronized admin systems
- Play Data Safety form
- release signing
- versioning
- crash testing
- accessibility checks

## Engineering calculation quality

Every engineering formula should have automated tests.

Minimum test coverage should include:

- normal case
- invalid input
- zero input
- boundary input
- known worked engineering example

Priority calculators include:

- single-phase current
- three-phase current
- kW and kVA conversion
- transformer full-load current
- transformer loading
- short-circuit estimate
- voltage drop
- power factor correction
- motor current
- battery sizing
- solar sizing
- CT ratio
- differential protection basics
- overcurrent basics
- earth-fault basics

Engineering calculations must clearly state assumptions and limitations.

## Local development

Open the project in Android Studio and let Gradle sync.

Run unit tests before release.

The project currently uses:

- Kotlin
- Jetpack Compose
- Material 3
- Room
- Firebase dependency BOM
- Retrofit
- Moshi
- Coil
- Coroutines

## Release rule

Do not publish a Play Store release until:

- real Firebase Authentication works
- Google Sign-In verifies real Google credentials
- password reset uses Firebase verified email flow
- admin authorization is backend enforced
- user records are owner-isolated
- Google Sheets sync performs a verified remote write
- report export creates real files
- privacy and account deletion are implemented
- engineering calculation tests pass
