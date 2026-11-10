
package com.punit.reimbursement.servlet;

import com.punit.reimbursement.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        // Step 1: Get login details
        String code = req.getParameter("employee_code");
        String pass = req.getParameter("password");

        // Step 2: Validate input
        if (code == null || pass == null ||
                !code.matches("\\d{1,10}") ||
                pass.isEmpty() ||
                pass.length() < 4 ||
                pass.getBytes(StandardCharsets.UTF_8).length > 72) {

            resp.sendRedirect("login.html?error=invalid");
            return;
        }

        int employeeCode;

        try {
            employeeCode = Integer.parseInt(code);
        } catch (NumberFormatException e) {
            resp.sendRedirect("login.html?error=invalid");
            return;
        }

        // Step 3: Fetch employee details
        String sql = "SELECT EmployeeCode, Password, Name, " +
                "Designation, ContactNumber, ReportingManagerCode " +
                "FROM EmployeeMaster WHERE EmployeeCode=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, employeeCode);

            try (ResultSet r = p.executeQuery()) {

                if (!r.next()) {
                    resp.sendRedirect("login.html?error=invalid");
                    return;
                }

                String storedPassword = r.getString("Password");
                boolean passwordValid = false;

                // Step 4: Verify password
                if (storedPassword != null) {

                    // Check BCrypt password
                    if (storedPassword.startsWith("$2a$") ||
                            storedPassword.startsWith("$2b$") ||
                            storedPassword.startsWith("$2y$")) {

                        try {
                            passwordValid =
                                    BCrypt.checkpw(pass, storedPassword);
                        } catch (IllegalArgumentException e) {
                            passwordValid = false;
                        }

                    } else {

                        // Support old plain-text passwords temporarily
                        passwordValid = storedPassword.equals(pass);

                        // Automatically convert to BCrypt
                        if (passwordValid) {

                            String hashedPassword =
                                    BCrypt.hashpw(
                                            pass,
                                            BCrypt.gensalt(12)
                                    );

                            try (PreparedStatement update =
                                    c.prepareStatement(
                                            "UPDATE EmployeeMaster " +
                                            "SET Password=? " +
                                            "WHERE EmployeeCode=?")) {

                                update.setString(1, hashedPassword);
                                update.setInt(2, employeeCode);
                                update.executeUpdate();
                            }
                        }
                    }
                }

                // Step 5: Reject invalid credentials
                if (!passwordValid) {
                    resp.sendRedirect("login.html?error=invalid");
                    return;
                }

                // Step 6: Prevent session fixation
                HttpSession oldSession = req.getSession(false);

                if (oldSession != null) {
                    oldSession.invalidate();
                }

                // Step 7: Create secure login session
                HttpSession session = req.getSession(true);

                session.setMaxInactiveInterval(15 * 60);

                session.setAttribute("employeeCode",
                        r.getInt("EmployeeCode"));

                session.setAttribute("employeeName",
                        r.getString("Name"));

                session.setAttribute("designation",
                        r.getString("Designation"));

                session.setAttribute("contactNumber",
                        r.getString("ContactNumber"));

                int managerCode =
                        r.getInt("ReportingManagerCode");

                session.setAttribute("reportingManagerCode",
                        r.wasNull() ? null : managerCode);

                // Step 8: Redirect after successful login
                resp.sendRedirect("raise_claim.html");
            }

        } catch (SQLException e) {

            throw new ServletException(
                    "Login/database error", e
            );
        }
    }
}
