# 🧠 Mind Mapping Application

A powerful JavaFX-based Mind Mapping Application that allows users to create, save, and load interactive mind maps. Designed for students, professionals, and anyone who wants to visually organize their ideas efficiently.

---

## 🚀 Features

- **User Authentication**:
  - Secure Sign-up and Sign-in system.
  - Admin access with password protection for managing user data.

- **Mind Map Creation**:
  - Drag-and-drop node creation.
  - Intuitive design with easy-to-use UI components.

- **Mind Map Persistence**:
  - Save and load mind maps using a database for seamless access.
  - Admin can delete user data if needed.

- **Interactive Admin Panel**:
  - Manage user data and mind maps.
  - User-friendly interface for easy administration.

---

## 🛠️ Technologies Used

- **Programming Language**: Java (JavaFX)
- **Database**: MySQL
- **Libraries/Tools**:
  - JavaFX for front-end UI.
  - JDBC for database connectivity.
  - Gson for JSON serialization and deserialization.

---

## 📂 Project Structure

```
Mind_Map_Project/
│
├── src/main/java/
│   ├── com/example/mind_map/
│   │   ├── HelloApplication.java     # Main application entry point
│   │   ├── DatabaseUtils.java        # Handles database operations
│   │   ├── AdminPanel.java           # Admin functionalities
│   │   ├── MindMapCanvas.java        # Mind map canvas logic
│   │   └── TestDatabaseConnection.java # Test database connection utility
│
├── src/main/resources/
│   ├── styles.css                    # Styling for the UI
│   └── path_to_image/                # Image resources used in the project
│
├── target/                           # Compiled files
├── pom.xml                           # Maven dependencies and build config
└── README.md                         # Project documentation
