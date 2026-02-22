# 🏃‍♂️ RunCounter – Offline Fitness Tracker (Android)

RunCounter is an Android application built with **Kotlin** and **Jetpack Compose** that tracks running sessions, distance, and duration — even in **low or no network connectivity**.  
It demonstrates an **offline-first architecture**, **clean MVI structure**, and **reliable background synchronization**, suitable for real-world environments like field operations or mobility tracking.

---

## ✨ Key Features

- 📍 **Real-Time Run Tracking**
  - Records live location and calculates total distance and pace.
  - Uses a **foreground service** for uninterrupted tracking while the app runs in the background.

- 🔄 **Offline-First Data Handling**
  - Saves run sessions locally using **Room Database**.
  - Automatically **syncs unsent data** with the server once connectivity is restored using **WorkManager**.

- 💾 **Data Synchronization Logic**
  - Queues user actions (e.g., new run) in an **database table** when offline.  
  - A scheduled **WorkManager job** with `NetworkType.CONNECTED` pushes updates once the network is available.
  - Ensures reliable, idempotent API calls and consistent data between local and remote sources.

- 🧩 **Modern Architecture**
  - Implements **MVI** + **Repository Pattern** for clean separation of concerns.
  - Uses **Koin** for dependency injection and **Kotlin Coroutines / Flow** for reactive, lifecycle-aware data streams.
  - Follows **Clean Architecture** principles to improve testability and maintainability.

- 🧭 **Jetpack Compose UI**
  - Entirely built with **Compose** for declarative and reactive UI.
  - Integrates with **StateFlow** and **ViewModel** for real-time state updates.
  - Simple, modern design with focus on performance and user experience.

- 🔔 **Notifications**
  - Provides real-time progress notifications while tracking runs in the background.

---

## 🧠 What This Project Demonstrates

- Reliable **offline-first architecture** for poor or unstable network conditions.  
- Efficient **background synchronization** using WorkManager.  
- Modern Android stack with **Jetpack Compose**, **Kotlin Coroutines**, and **Koin**.  
- Adherence to **clean architecture** and **SOLID principles**.  
- Focus on **user experience, data reliability, and scalability**.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVI + Repository Pattern |
| Dependency Injection | Koin |
| Database | Room |
| Background | WorkManager, Foreground Service |
| Networking | Ktor Client |
| Async | Kotlin Coroutines, Flow |
| Testing | JUnit, MockK |
