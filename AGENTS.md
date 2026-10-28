# AGENTS.md — Repository Instructions for AI Agents

> **Scope:** This document is automatically loaded by AI coding assistants (Antigravity, Cursor, Claude Code, Copilot, Windsurf) working within this workspace.

---

## 1. Project Overview

- **Project:** **SmartLib – College Library Management System**
- **Course:** PBCST304 – Object Oriented Programming
- **Institution:** College of Engineering Trivandrum (CET), APJ Abdul Kalam Technological University (KTU)
- **Tech Stack:** Java 17 LTS, Java Swing/AWT GUI, MySQL 8.0, JDBC (DAO Architecture), Maven, BCrypt, FlatLaf.
- **Documentation & Report:** Full academic report written in modular LaTeX under `report/`.

---

## 2. Working on the Course Project Report (`report/`)

When editing, compiling, or extending the LaTeX report, **you MUST strictly follow [report/AI_AGENT_GUIDELINES.md](file:///home/adhithyan-ks/Projects/Java/SmartLib/report/AI_AGENT_GUIDELINES.md)**.

### Crucial Report Invariants:
1. **Author Sequence:** Strictly ordered by KTU ID:
   1. Derin Jose Sanjith (`TVE25CS048`)
   2. Jenil Prince (`TVE25CS064`)
   3. Prathyush Roy P (`TVE25CS105`)
   4. Abhinand V P (`LTVE25CS142`)
   5. Adhithyan K S (`LTVE25CS143`)
2. **Title:** Must remain `SmartLib - College Library Management System`.
3. **Table Column Collision Prevention:**
   - Never declare raw `p{width}` with text or monospace tokens.
   - Always declare: `>{\raggedright\arraybackslash}p{...}` or use `tabularx` with `X` column.
   - Chapter 6 UI test tables must preserve standard widths (`p{1.6cm}`, `p{2.1cm}`, `p{2.5cm}`, `p{3.1cm}`, `p{3.1cm}`, `p{1.4cm}`).
   - Insert `\allowbreak` in long file paths and package identifiers.
4. **Build & Deliverable Sync SOP:**
   - Compile: `cd report && ~/.local/bin/tectonic --keep-intermediates main.tex`
   - Check warnings for `Overfull \hbox` (>15pt indicates layout break).
   - Visually verify modified pages: `pdftoppm -png -r 150 -f <page> -l <page> report/main.pdf /tmp/verify && rm -f /tmp/verify*`
   - Sync deliverables to both destinations:
     ```bash
     cp report/main.pdf report/SmartLib_Course_Project_Report.pdf && cp report/main.pdf SmartLib_Course_Project_Report.pdf
     ```

---

## 3. Working on the Java Application (`src/`)

- **Root Package:** `com.college.library`
- **Architecture Layers:**
  - `model`: Domain entities (`Book`, `Student`, `Librarian`, `BorrowTransaction`, `Reservation`, `BorrowRequest`).
  - `dao`: Data Access Object interfaces (`BookDAO`, `StudentDAO`, etc.) extending generic `CrudDAO<T, ID>`.
  - `dao.impl`: Concrete JDBC implementations using `DatabaseConnection.getInstance().getConnection()`.
  - `service`: Business logic layer orchestrating DAOs and validating business rules.
  - `gui`: Swing/AWT desktop interface (`MainApplication`, `LoginPanel`, `LibrarianDashboardPanel`, `StudentDashboardPanel`, `BorrowPanel`, etc.).
  - `util`: Utility classes (`BCryptUtil`, `DatabaseConnection`, `TableSelectionHelper`).
- **Build & Run:**
  - Build: `./mvnw clean compile` (or `.\mvnw.cmd clean compile` on Windows)
  - Run Application: `./mvnw exec:java -Dexec.mainClass="com.college.library.gui.MainApplication"`
  - Run Tests: `./mvnw test`
