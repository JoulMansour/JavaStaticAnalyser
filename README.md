# Sjava Static Analyzer & Verifier 🛡️

A comprehensive Java-based compiler-verifier designed to parse and validate a simplified version of Java (Sjava). This project demonstrates advanced knowledge of **Regex**, **Recursive Logic**, and **Error Handling**.

## Core Functionalities
* **Lexical Analysis:** Uses sophisticated Regular Expressions to tokenize and validate variable declarations, method signatures, and control structures.
* **Scope Management:** Implements a hierarchical scope-tree to handle nested blocks (if/while) and ensure proper variable visibility and lifetime.
* **Syntax Validation:** Detects illegal syntax, type mismatches, and improper method calls.
* **Robust Exception Handling:** Features a custom hierarchy of exceptions to provide specific feedback on code violations.

## Technical Deep Dive
### 1. Scope & Memory Logic
The verifier tracks global vs. local variables using a `Scope` object system. It ensures that variables are initialized before use and prevents illegal shadowing or redeclarations within the same scope.

### 2. The Regex Engine
Instead of a standard library parser, this project utilizes custom-built Regex patterns to handle:
* Method declarations: `void foo(int a, String b) { ... }`
* Variable assignment logic (including final variables).
* Complex boolean expressions for control flow.

### 3. Design Patterns
* **Singleton/Factory:** Used for managing global settings and scope generation.
* **Composite Pattern:** For representing the nested structure of the code blocks.

## Usage
The program exits with code 0 if the code is valid, 1 if the code is invalid, and 2 if there is a system/IO error.
