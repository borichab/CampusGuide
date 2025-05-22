# Campus Guide

### Table of Contents
1. [Introduction](#introduction)
2. [Tech Stack](#tech-stack)
3. [Features](#features)
4. [Installation](#installation)
5. [Usage](#usage)
6. [Contributors](#contributors)
7. [Problem Statement](#problem-statement)
8. [References](#references)

### Introduction
The **[Campus Guide](https://github.com/borichab/CampusGuide)** application is a versatile mobile assistant designed to cater to the needs of students, faculty members, and visitors within a campus environment. Developed using Android, it provides a robust framework for enhancing campus navigation, facilitating interactive learning experiences, and providing easy access to course-related information.

### Tech Stack
- **Platform**: Android (Java)
- **APIs**: Google Maps API, Nearby Connections API
- **Database**: Firebase
- **Tools**: Git, Android Studio

### Features
- **Interactive Campus Map**: Utilizes the Google Maps API to offer a dynamic map interface highlighting key campus buildings and locations.
- **Walkie-Talkie for Campus Tours and Lectures**: A unique feature allowing a designated Host to communicate with multiple clients using Android devices. This is powered by Google's Nearby Connection API.
- **Course Information**: Provides access to a catalog of courses categorized by subject and semester.

### Screenshots
#### Home Page
<img src="https://github.com/user-attachments/assets/bc2f2710-7eef-4b28-b617-01fc66817216" alt="Home Page 1" width="200"/>
<img src="https://github.com/user-attachments/assets/365ce2f6-0b87-456f-aeb3-a44a220d248a" alt="Home Page 2" width="200"/>
<img src="https://github.com/user-attachments/assets/ce7600e0-bf47-4840-b635-fd1110f3433c" alt="Home Page 1" width="200"/>

- **Map Icon**: Directs users to an interactive campus map.
- **WalkeiTalkei Icon**: Allows users to join or host a live communication session.
- **Course Info Icon**: Provides a list of subjects organized by course name and semester.

#### Map Page
<img src="https://github.com/user-attachments/assets/9a2e0d3f-d9e5-4e6e-aba6-a1b66c9dd9db" alt="Switch to satellite view" width="200"/>
<img src="https://github.com/user-attachments/assets/9c897faf-e15f-4e65-a8a5-38978b7bfe03" alt="Live User location" width="200"/>
<img src="https://github.com/user-attachments/assets/87c38064-9446-4290-ab52-e22ac55b4937" alt="Map View" width="200"/>
<img src="https://github.com/user-attachments/assets/bc399b9d-b0f9-47e1-9b73-c7ad57ed4044" alt="Building Information" width="200"/>

- **Initial Map View**: Overview of the campus area.
- **Live Location**: Real-time tracking of user location.
- **Satellite View**: Option to switch between Normal and Satellite views.
- **Building Information Dialog**: Detailed information about selected buildings.

#### WalkeiTalkei Page
<img src="https://github.com/user-attachments/assets/8cdf416c-079f-4fbe-a0c3-f966f3f94a90" alt="Host side Interface" width="200"/>
<img alt="Client-side Interface" src="https://github.com/user-attachments/assets/b69cb504-b4d9-4c6b-945b-c1c0e32ddf18" width="200"/>
<img alt="Client-side Interface" src="https://github.com/user-attachments/assets/ba04e590-2d20-4d02-9e63-66be08fcb065" width="200"/>

- **Host Interface**: Hosts can initiate and terminate the discovery process.
- **Client View**: Clients can connect and communicate with the host.

#### Managing Connected Clients
The host retains control over the discovery process even when multiple clients are connected. This flexibility allows the host to manage the communication session effectively.

<img src="https://github.com/user-attachments/assets/5b7e0c24-3dab-4b0c-8fa5-85ad7b845a4c" alt="Client interface" width="200" />
<img src="https://github.com/user-attachments/assets/c476b6c9-9558-429f-9e3d-c13ce6f685e7" alt="Host visible to client" width="200" />
<img src="https://github.com/user-attachments/assets/9430b5ad-de8d-4962-89d4-929906ebac8c" alt="Host connected interface" width="200" />

- **Host Managing Clients**
  - Multiple clients can be connected simultaneously.
  - The host can stop and start the discovery process as needed.

- **Broadcasting Messages**
  - After stopping discovery, the host is ready to broadcast messages to all connected clients. If a new client wishes to join, the host can restart the discovery process, ensuring seamless connections.

- **Broadcast Mode**
  - Host is in broadcasting mode, ready to convey information to all connected users.
  - New users can join the session if the host restarts discovery.

#### Course Information Page

<img src="https://github.com/user-attachments/assets/bcfe84f3-27e0-45d0-a4b9-4fc2ab111ab3" alt="Selecting smester" width="200" />
<img src="https://github.com/user-attachments/assets/76385e52-0c72-4296-83b3-d7f00c1f1034" alt="Selecting course" width="200" />

- **Course Selection**: Dropdown menu listing various courses.
- **Semester-Specific Subjects**: Displays subjects taught during the selected semester.

### Installation
1. Clone the repository:
    ```bash
    git clone https://github.com/borichab/campus-guide.git
    ```
2. Open the project in Android Studio.
3. Build and run the project on an Android device or emulator.

### Usage
1. Open the app on your Android device.
2. Navigate through the Home Page to access different features.
3. Use the interactive map for campus navigation.
   - **Note**: Don't forget to use your own Google Map API key in the file [CampusGuide/app/src/main/AndroidManifest.xml](https://github.com/borichab/CampusGuide/blob/master/app/src/main/AndroidManifest.xml)
5. Join or host a session using the WalkeiTalkei feature.
6. Access course information through the Course Info section.

### Contributors
- **Bhartkumar Boricha**
- 📧 Contact: bhartpboricha@gmail.com

### Problem Statement
Modern educational institutions face several challenges:
1. **Navigation and Orientation**: Difficulty in locating specific campus destinations.
2. **Enhanced Campus Interaction**: Need for engaging methods to deliver tours, lectures, and announcements.
3. **Access to Course Information**: Quick access to comprehensive course details and schedules.
4. **Real-time Communication**: Establishing efficient one-to-many communication channels in large campus environments.

The Campus Guide app aims to address these challenges by providing a user-friendly platform combining interactive maps, real-time communication, and easy access to course information.

### References
- Android Developers: [Behavior changes: Apps targeting Android 13 or higher](https://developer.android.com/about/versions/13/behavior-changes-13)
- Android Developers: [ContextCompat](https://developer.android.com/reference/androidx/core/content/ContextCompat#checkSelfPermission)
- Android Developers: [Create P2P connections with Wi-Fi Direct](https://developer.android.com/training/connect-devices-wirelessly/wifi-direct)
- Android Developers: [Request App Permissions](https://developer.android.com/training/permissions/requesting)
- Patrawala, M.: [Walkie-Talkie on GitHub](https://github.com/murtaza98/Walkie-Talkie/tree/master)
- Technology, S.: [Android WiFi P2P Tutorial on YouTube](https://www.youtube.com/playlist?list=PLFh8wpMiEi88SIJ-PnJjDxktry4lgBtN3)
- [Request a permission in Android on YouTube](https://youtu.be/x38dYUm7tCY?si=X870HT7jneRoJsLe)
