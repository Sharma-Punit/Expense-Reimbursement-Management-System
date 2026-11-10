# Expense Reimbursement Management System - Java
Java 17 + Jakarta Servlet 6 + JDBC + MySQL + Tomcat 10.1 Maven WAR project.

## Included
- Employee/manager login
- Submit reimbursement claim
- View own claim history
- Manager view of claims assigned to them
- Approve, reject or refer back
- Expense dropdown loaded from ExpenseMaster

## Setup
1. Start MySQL in XAMPP.
2. Import `sql/schema.sql` in phpMyAdmin (if the reimbursement schema is not already imported).
3. Demo credentials: employee `1001` / `1234`; manager `1002` / `1234`.
4. If your MySQL credentials differ, set environment variables `DB_USER`, `DB_PASSWORD`, and optionally `DB_URL`.
5. From the folder containing pom.xml run: `mvn clean package`.
6. Copy `target/ExpenseReimbursementJava.war` to Tomcat 10.1 `webapps` folder.
7. Start Tomcat and open `http://localhost:8080/ExpenseReimbursementJava/`.

## Important
The provided schema stores demo passwords as plaintext to remain compatible with the supplied PHP schema. This is for local demonstration only; implement password hashing before production use.
The stylesheet included is a clean fallback, not an exact copy of the original UI because the original `style.css` file was not supplied in this chat. Replace `src/main/webapp/style.css` with your original `public/style.css` to restore its exact styling.
The original PHP search page source was not provided, so search functionality is not included in this Java package.
