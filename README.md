# MySQL Task Scheduler (JavaFX)

A desktop application to schedule shell commands and log their execution to MySQL.

## Prerequisites
1.  **Java JDK 17+** installed.
2.  **Maven** installed (`mvn` command available).
3.  **MySQL Database** running.

## Database Setup
1.  Create a database named `scheduler_db` (or update `DatabaseConfig.java`).
2.  Run the schema script located at `src/main/resources/schema.sql`.
    ```sql
    CREATE DATABASE scheduler_db;
    USE scheduler_db;
    -- Run content of schema.sql
    ```

## Configuration
Open `src/main/java/com/scheduler/db/DatabaseConfig.java` and update:
- `DB_URL`
- `DB_USER`
- `DB_PASS`

## How to Run
Open a terminal in this directory and run:

```bash
mvn javafx:run
```

## Features
- **Add Task**: Schedule commands by Interval (minutes) or Fixed Time (HH:mm).
- **Execution**: Runs commands in background using `ProcessBuilder`.
- **Logging**: Automaticaly logs Start Time, End Time, Exit Code, and Output to MySQL `execution_logs` table.
