# 🎸 AI Chord Finder App

An Android application that detects musical chords from songs using audio processing and machine learning.

---

## 🚀 Overview

AI Chord Finder is a full-stack application that enables users to fetch songs and automatically extract chords in real-time.

The system combines:

* 📱 Android frontend (Kotlin)
* 🌐 Backend (FastAPI - Python)
* 🎧 Audio processing (Spleeter)
* 🤖 Machine learning-based chord recognition

---

## ⚡ Features

* Fetch songs using YouTube API
* Extract and process audio from video
* Real-time chord detection
* Backend-powered inference pipeline
* Clean and intuitive Android UI

---

## 🧠 How It Works

1. **Fetch Song**
   The user selects a song, and the app fetches video data using the YouTube API.

2. **Audio Extraction & Processing**
   Audio is extracted from the video and sent to the backend.

3. **Source Separation**
   Spleeter (by Deezer) separates audio into components to isolate melodic instruments.

4. **Chord Detection**
   Machine learning models (inspired by ISMIR research) analyze the processed audio and detect chords.

5. **Display Results**
   Detected chords are returned via API and displayed in the Android app.

---

## 🏗️ Architecture

```
Android App (Kotlin)
        ↓
   REST API (Retrofit)
        ↓
Backend (FastAPI - Python)
        ↓
Audio Processing (Spleeter)
        ↓
Chord Recognition Model
        ↓
Response (Chords)
        ↓
Android UI Display
```

---

## 🛠️ Tech Stack

### 📱 Android

* Kotlin
* Jetpack Compose / XML
* Retrofit

### 🌐 Backend

* Python
* FastAPI

### 🎧 Audio Processing

* Spleeter (Deezer)

### 🤖 Machine Learning

* Chord recognition model (inspired by ISMIR research)

---

## 📸 Screenshots

<p align="center">
![2](https://github.com/user-attachments/assets/c562792a-28b5-48ef-b20f-d1bdc354ccea)
![3](https://github.com/user-attachments/assets/2a306f82-0056-47fa-a7e1-033018aa0cde)
![4](https://github.com/user-attachments/assets/1ab407d5-03b9-4b19-bef0-688f0278560d)
![1](https://github.com/user-attachments/assets/e974adaf-9ea2-4edb-ae5e-aa666f6b576c)

</p>
---

## 🎥 Demo

> Add demo video or GIF here (highly recommended)

---
## 🔑 API Endpoint

**POST /analyze**

* Input: Audio file
* Output: Detected chords

---

## 🚧 Challenges Faced

* Extracting usable audio from video sources
* Improving chord detection accuracy using source separation
* Managing latency for near real-time results

---

## 📈 Future Improvements

* Real-time live chord detection
* Offline model support
* Timeline-based chord visualization
* Support for multiple instruments

---
