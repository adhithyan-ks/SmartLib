# SmartLib - College Library Management System

SmartLib is a modern, Java-based Desktop Application designed for College Libraries. It manages both a Librarian backend and a Student frontend.

## 🚀 Setup Guide (Windows)

Follow these step-by-step instructions to set up SmartLib on a fresh Windows computer.

### 1. Prerequisites Checklist
**Required Software:**
- **Java JDK (Version 17 or higher)**
- **MySQL Server (Version 8.0 or higher)**

**Optional Software:**
- **MySQL Workbench** (Highly recommended for executing SQL scripts and viewing the database visually)
- **Maven** (Optional: A Maven wrapper `mvnw.cmd` is included in the project, so you do not need to install Maven manually)

---

### 2. Install & Configure Java (JDK 17)

1. Download **Java SE Development Kit 17** (or newer) from the [Oracle Website](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [Adoptium](https://adoptium.net/).
2. Run the installer and complete the installation.
3. **Configure Environment Variables:**
   - Open Windows Start Menu, search for **"Environment Variables"**, and select "Edit the system environment variables".
   - Click the **Environment Variables...** button.
   - Under *System variables*, click **New...**
     - Variable name: `JAVA_HOME`
     - Variable value: `C:\Program Files\Java\jdk-17` (or your actual installation path)
   - Find the `Path` variable under *System variables*, click **Edit...**, then **New**, and add: `%JAVA_HOME%\bin`
4. **Verify Installation:**
   Open PowerShell or Command Prompt and run:
   ```powershell
   java -version
   javac -version
   ```
   Both commands should output version `17`.

---

### 3. Install & Configure MySQL Server

1. Download the **MySQL Installer** from the [MySQL Website](https://dev.mysql.com/downloads/installer/).
2. Run the installer. You can choose the **"Developer Default"** or **"Server only"** setup. Include **MySQL Workbench** if you prefer a visual database manager.
3. During configuration:
   - Keep the default port as **3306**.
   - Create a strong Root Password (e.g., `root123`). Remember this password!
4. Start the MySQL Server (it usually starts automatically as a Windows Service).

---

### 4. Database Setup

1. Open **MySQL Workbench** or the **MySQL Command Line Client**.
2. Connect to your local server using the root password you created.
3. Run the following commands to create the database:
   ```sql
   CREATE DATABASE smartlib;
   USE smartlib;
   ```
4. Find the SQL scripts located in the project's `src/main/resources/` directory.
5. Execute the contents of `src/main/resources/schema.sql` first to create the tables.
6. Execute the contents of `src/main/resources/sample_data.sql` next to populate the database with dummy data, books, and default accounts.

---

### 5. Project Configuration

SmartLib needs to know your MySQL credentials to connect to the database.

1. Navigate to the project directory: `src/main/resources/`
2. You will find a file named `database.properties.example`.
3. Make a copy of this file and rename it to `database.properties` (Make sure it is in the same directory).
   *(Note: `database.properties` is ignored by Git to protect your passwords!)*
4. Open `database.properties` and update the `db.password` to match your MySQL root password:
   ```properties
   db.url=jdbc:mysql://localhost:3306/smartlib
   db.user=root
   db.password=YOUR_MYSQL_PASSWORD
   ```

---

### 6. Cloning & Running the Application

Open PowerShell and follow these commands:

**Clone the repository:**
```powershell
git clone https://github.com/jenilprince/SmartLib.git
cd SmartLib
```

**Run the Maven Test Suite:**
SmartLib includes a built-in Maven Wrapper (`mvnw.cmd`). You can run tests to verify everything is working.
```powershell
.\mvnw.cmd test
```

**Launch SmartLib:**
To compile and launch the application GUI:
```powershell
.\mvnw.cmd compile
.\mvnw.cmd exec:java "-Dexec.mainClass=com.college.library.gui.MainApplication"
```

---

### 7. First-Time Login

If you imported the `sample_data.sql` correctly, you can log in using these default credentials:

**Librarian Login:**
- **Username:** `admin`
- **Password:** `password123`

**Student Login:**
- **KTU ID:** `TVE25CS064`
- **Password:** `password123`

---

## 🛠️ Troubleshooting Common Errors

**Error: `java is not recognized as an internal or external command`**
- **Fix:** Your `JAVA_HOME` or `Path` environment variable is missing or incorrect. Re-check Step 2. Restart PowerShell after making changes to environment variables.

**Error: `Communications link failure` or `Connection refused: connect`**
- **Fix:** SmartLib cannot reach the MySQL server.
  - Verify that the MySQL service is running in Windows Services (`services.msc` -> look for MySQL80).
  - Ensure the port is `3306`.
  - Check if the database URL in `database.properties` is correct.

**Error: `Access denied for user 'root'@'localhost'`**
- **Fix:** The password in `database.properties` is incorrect. Update it to match your MySQL server password.

**Error: `Unknown database 'smartlib'`**
- **Fix:** You forgot to create the database. Run `CREATE DATABASE smartlib;` in MySQL Workbench.

**Error: `Table 'smartlib.librarians' doesn't exist`**
- **Fix:** You created the database but forgot to run `schema.sql`. Import and execute the schema file.
