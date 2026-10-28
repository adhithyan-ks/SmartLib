# AI Agent Guidelines for SmartLib Course Project Report

> **Target Audience:** AI Coding Agents (Antigravity, Cursor, Claude Code, Copilot, Windsurf, Aider) and human team members prompting them.  
> **Purpose:** Maintain strict formatting standards, prevent layout/table collisions, protect author metadata, and ensure reliable PDF compilation without regressions.

---

## 1. Non-Negotiable Invariants

Whenever an AI agent touches this codebase or report, the following invariants **must never be altered or regressed**:

1. **Author Sequence (KTU ID Order):**
   KTU regulations require authors to be listed in ascending order of their KTU registration numbers. Maintain this exact order everywhere (title page, hyperref metadata, documentation):
   1. **Derin Jose Sanjith** (`TVE25CS048`)
   2. **Jenil Prince** (`TVE25CS064`)
   3. **Prathyush Roy P** (`TVE25CS105`)
   4. **Abhinand V P** (`LTVE25CS142`)
   5. **Adhithyan K S** (`LTVE25CS143`)

2. **Project Title:**
   The official report title is strictly:
   $$\textbf{SmartLib - College Library Management System}$$
   Do not shorten to just "SmartLib" or rename the project.

3. **Faculty Guidance Block:**
   The guidance block on the title page (`report/sections/01_title_abstract.tex`) reflects the appointed faculty guide. Do not remove or alter this block without explicit user instruction.

4. **Codebase Synchronization & Truthfulness:**
   - Code listings in `report/sections/10_appendices.tex` and architectural descriptions in `05_system_design.tex` / `06_implementation.tex` **must match the actual Java code** in `src/main/java/com/college/library/`.
   - Never hallucinate non-existent service methods, DAO interfaces, or SQL tables.

5. **Dual PDF Deliverable Sync:**
   The project maintains two synchronized copies of the final PDF report:
   - Root copy: `SmartLib_Course_Project_Report.pdf`
   - Report directory copy: `report/SmartLib_Course_Project_Report.pdf`
   Every compilation **must** copy the generated `report/main.pdf` to both destinations.

---

## 2. Report Architecture & File Map

All report sources reside under `report/`:

```text
report/
├── main.tex                       # Master root document (preamble, geometry, package imports)
├── references.bib                 # IEEE-style BibTeX references
├── SmartLib_Course_Project_Report.pdf # Deliverable PDF copy inside report/
├── figures/                       # UI screenshots (PNG)
├── newfigures/                    # Architecture, Class Diagram, ER Diagram assets
├── logo/                          # Institution & university crests
└── sections/                      # Modular LaTeX sections
    ├── 01_title_abstract.tex      # Title page, KTU declaration, Abstract, TOC, LOF, LOT
    ├── 02_introduction.tex        # Chapter 1: Introduction, domain context, existing challenges
    ├── 03_objectives.tex          # Chapter 2: Problem statement, objectives, scope & boundaries
    ├── 04_requirements.tex        # Chapter 3: Functional/Non-functional requirements, Tech Stack
    ├── 05_system_design.tex       # Chapter 4: Architecture, UML Class Diagram, ER, UI Design
    ├── 06_implementation.tex      # Chapter 5: OOP principles, DAO pattern, transactions, BCrypt
    ├── 07_testing.tex             # Chapter 6: Manual UI test cases (Tables 6.1–6.9), Test summary (6.10)
    ├── 08_results.tex             # Chapter 7: Verification results, screenshots, execution traces
    ├── 09_conclusion.tex          # Chapter 8: Conclusion & roadmap for future work
    └── 10_appendices.tex          # Appendices: Setup guide, seed credentials, troubleshooting, source code
```

---

## 3. Critical LaTeX Table Formatting Guardrails

> [!CAUTION]
> **Why tables broke previously:**
> LaTeX's default fixed-width column specifier `p{width}` applies full justification. When cells contain long un-hyphenated strings (such as `\texttt{TC-UI-01}`, `BorrowTransactionDAO`, package coordinates, or file paths), LaTeX refuses to break them. If the column width is narrower than the text's natural width, the text **collides directly into adjacent columns** (e.g. `TC-UI-01Librarian`) or spills across the right page margin.

### Rule 3.1: Always Use `>{\raggedright\arraybackslash}` with Fixed Columns
Never declare raw `p{width}` for textual or code columns. Always prefix them with `>{\raggedright\arraybackslash}` from the `array` package to force clean left alignment and proper wrapping:

```latex
% BAD (causes text collision and ugly justification):
\begin{longtable}{p{1.1cm} p{2.0cm} p{2.5cm}}

% GOOD (ragged-right wrapping, no overlap):
\begin{longtable}{>{\raggedright\arraybackslash}p{1.6cm} >{\raggedright\arraybackslash}p{2.1cm} >{\raggedright\arraybackslash}p{2.5cm}}
```

### Rule 3.2: Chapter 6 UI Test Case Tables (6.1 – 6.9)
All manual test tables in `report/sections/07_testing.tex` follow this tested, margin-safe layout:
- **Font size:** `\footnotesize`
- **Padding:** `\setlength{\tabcolsep}{3pt}`
- **Column widths:**
  - `Test ID`: `>{\raggedright\arraybackslash}p{1.6cm}` (guarantees `TC-UI-xx` will never touch the next column)
  - `Feature`: `>{\raggedright\arraybackslash}p{2.1cm}`
  - `Input / Action`: `>{\raggedright\arraybackslash}p{2.5cm}`
  - `Expected Result`: `>{\raggedright\arraybackslash}p{3.1cm}`
  - `Actual Result`: `>{\raggedright\arraybackslash}p{3.1cm}`
  - `Status`: `>{\centering\arraybackslash}p{1.4cm}` (fits `PASS` and two-line `NOT TESTED`)

### Rule 3.3: Use `tabularx` for Wide & Matrix Tables
For tables spanning full page width (e.g., Table 3.2 Software Stack, Table A.1 Seed Credentials, Table A.2 Troubleshooting Matrix):
```latex
\begin{table}[h!]
    \centering
    \small
    \caption{Table Caption Here}
    \begin{tabularx}{\textwidth}{>{\raggedright\arraybackslash}p{4.2cm} >{\raggedright\arraybackslash}p{4.0cm} >{\raggedright\arraybackslash}X}
        \toprule
        \textbf{Header 1} & \textbf{Header 2} & \textbf{Header 3} \\
        \midrule
        Row data & Row data & Auto-wrapped text in X column \\
        \bottomrule
    \end{tabularx}
\end{table}
```

### Rule 3.4: Discretionary Breakpoints for Code & Paths
Long composite names or paths will overflow if not given break opportunities:
- File paths: `\texttt{src/main/}\allowbreak\texttt{resources/}\allowbreak\texttt{database.properties}`
- Maven coordinates: `com.mysql:\allowbreak mysql-connector-j:\allowbreak 9.4.0`
- Long class names in tables: `BorrowTransactionDAO\newline/ Impl` or `CrudDAO<Borrow-\newline Transaction, Integer>`

### Rule 3.5: Float Control & Header Ordering
- Do not let a floating table appear **above** its corresponding `\section` header.
- If a table floats to the top of a page ahead of its section title, insert `\clearpage` immediately before the `\section{...}` command.

---

## 4. Compilation & Verification Standard Operating Procedure (SOP)

Every AI agent modifying the report must execute this full cycle before marking the task complete:

### Step 1: Compile with `tectonic`
Run tectonic inside the `report/` directory:
```bash
cd report && ~/.local/bin/tectonic --keep-intermediates main.tex
```
*(If tectonic is unavailable, use `pdflatex main.tex && bibtex main && pdflatex main.tex && pdflatex main.tex`)*.

### Step 2: Audit Compiler Warnings
Check the compiler output for lines with:
```text
warning: Overfull \hbox (XXpt too wide) in paragraph ...
```
- Warnings under 5pt are usually imperceptible.
- Warnings exceeding **15pt** indicate a table column collision or text spilling into the margin that **must be fixed**.

### Step 3: Render and Visually Inspect Affected Pages
Never assume a compile without errors looks good visually. Render the modified page(s) to PNG:
```bash
# Example: Render page 28 (Table 6.1) to /tmp
pdftoppm -png -r 150 -f 28 -l 28 report/main.pdf /tmp/verify_page
```
Inspect the image using the agent's image viewing tool to verify:
- No text overlaps between columns.
- No text spilling past the right vertical line or margin.
- Captions and section headers appear above the respective tables.

### Step 4: Sync Deliverables
Once verified, copy the output PDF to both project deliverable paths:
```bash
cp report/main.pdf report/SmartLib_Course_Project_Report.pdf && cp report/main.pdf SmartLib_Course_Project_Report.pdf
```

### Step 5: Clean Up
Remove any intermediate verification images from `/tmp`:
```bash
rm -f /tmp/verify_*
```

---

## 5. Teammate Prompting Guide (Copy-Paste Prompts)

When prompting an AI agent to edit this report, include the reference to these guidelines. Here are recommended templates:

### Adding or Modifying a Test Case in Chapter 6:
> "Please add a new test case for [Feature Name] in Table [X.X] in `report/sections/07_testing.tex`. Ensure you follow the table column widths and `>{\raggedright\arraybackslash}` conventions in `report/AI_AGENT_GUIDELINES.md`. Recompile with tectonic, verify with pdftoppm, and sync the deliverable PDFs."

### Updating Architecture or Design Diagrams in Chapter 4:
> "Update the class diagram description in `report/sections/05_system_design.tex`. Ensure class names and relationships strictly match `src/main/java`. Follow `report/AI_AGENT_GUIDELINES.md` to avoid table or diagram overlaps, recompile with tectonic, and sync both deliverable PDFs."

### Adding Content to Appendices:
> "Add the troubleshooting steps for [Issue] to Table A.2 in `report/sections/10_appendices.tex`. Use `tabularx` with discretionary `\allowbreak` in all file paths as mandated by `report/AI_AGENT_GUIDELINES.md`. Recompile, visually verify, and sync the PDFs."
