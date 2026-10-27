# SmartLib - Course Project Report

**Course:** PBCST304 – Object Oriented Programming  
**Institution:** College of Engineering Trivandrum (CET), APJ Abdul Kalam Technological University (KTU)  
**Authors:**
- Adhithyan K S
- Abhinand V P
- Prathyush
- Jenil Prince
- Derin Jose Sanjith

---

## 📄 Overview

This directory contains the complete LaTeX source code, figures, database diagrams, screenshots, and bibliography for the **SmartLib Course Project Report**, prepared in accordance with the guidelines outlined in `REPORT.md`.

---

## 📁 Directory Structure

```text
report/
├── main.tex                       # Master LaTeX document tying all sections together
├── references.bib                 # BibTeX references (IEEE citation standard)
├── SmartLib_Project_Report.pdf    # Compiled high-resolution PDF report
├── figures/                       # Screenshots and figures embedded in the report
│   ├── screenshot_login.png       # Login portal (Librarian & Student modes)
│   ├── screenshot_dashboard.png   # Librarian analytics dashboard
│   ├── screenshot_books.png       # Book catalog & inventory management
│   ├── screenshot_students.png    # Student roster & clearance desk
│   ├── screenshot_borrow.png      # Circulation desk with Active Borrowings
│   ├── screenshot_reservation.png # Title reservation queue desk
│   └── screenshot_student_dashboard.png # Student self-service portal
└── sections/                      # Modular LaTeX sections
    ├── 01_title_abstract.tex      # 1. Title Page, 2. Abstract, 3. TOC, 4. LOF, 5. LOT
    ├── 02_introduction.tex        # 6. Introduction & Background
    ├── 03_objectives.tex          # 7. Problem Statement & Objectives
    ├── 04_requirements.tex        # 8. Requirements Specification (FR, NFR, Environment)
    ├── 05_system_design.tex       # 9. System Design (Architecture, Class Diagram, ER, UI)
    ├── 06_implementation.tex      # 10. Implementation & Core OOP Principles
    ├── 07_testing.tex             # 11. Testing & Test Cases (Manual Functional Testing)
    ├── 08_results.tex             # 12. Results, Screenshots & Execution Traces
    ├── 09_conclusion.tex          # 13. Conclusion & Future Enhancements
    └── 10_appendices.tex          # 15. Appendices (User Manual & Source Code)
```

---

## 🛠️ How to Compile the Report

### Option 1: Overleaf (Recommended for Cloud Compilation)
1. Zip the entire `report/` folder:
   ```bash
   cd report && zip -r SmartLib_Report.zip .
   ```
2. Go to [Overleaf](https://www.overleaf.com/).
3. Click **New Project** $\to$ **Upload Project**, and select `SmartLib_Report.zip`.
4. Set compiler to **pdfLaTeX** or **XeLaTeX**, and click **Recompile**.

### Option 2: Local Compilation with `pdflatex` & `bibtex`
If you have a local TeX Live or MiKTeX installation:
```bash
cd report
pdflatex main.tex
bibtex main
pdflatex main.tex
pdflatex main.tex
```

### Option 3: Local Compilation with `tectonic`
Tectonic is a modern, self-contained TeX engine that automatically downloads packages:
```bash
cd report
tectonic main.tex
```
The output PDF will be generated as `main.pdf`.

---

## 📋 Compliance with `REPORT.md` Section Guidelines

| Section # | Guideline Section | LaTeX Source Location |
|---|---|---|
| **1** | Title Page | `sections/01_title_abstract.tex` |
| **2** | Abstract | `sections/01_title_abstract.tex` |
| **3** | Table of Contents | `sections/01_title_abstract.tex` (`\tableofcontents`) |
| **4** | List of Figures | `sections/01_title_abstract.tex` (`\listoffigures`) |
| **5** | List of Tables | `sections/01_title_abstract.tex` (`\listoftables`) |
| **6** | Introduction | `sections/02_introduction.tex` |
| **7** | Problem Statement & Objectives | `sections/03_objectives.tex` |
| **8** | Requirements Specification | `sections/04_requirements.tex` |
| **9** | System Design (Architecture, Classes, ER, UI) | `sections/05_system_design.tex` |
| **10** | Implementation (OOP concepts, JDBC, Algorithms) | `sections/06_implementation.tex` |
| **11** | Testing & Test Cases (Manual UI Testing) | `sections/07_testing.tex` |
| **12** | Results (Screenshots, Traces, Summaries) | `sections/08_results.tex` |
| **13** | Conclusion & Future Enhancements | `sections/09_conclusion.tex` |
| **14** | References | `references.bib` via `main.tex` |
| **15** | Appendices (User Manual & Code Listings) | `sections/10_appendices.tex` |
