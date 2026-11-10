
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        String code = req.getParameter("employee_code");
        String pass = req.getParameter("password");

        if (code == null || pass == null ||
                !code.matches("\\d+") || pass.isEmpty()) {
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
                boolean passwordValid;

                // Verify an existing BCrypt password
                if (storedPassword != null &&
                        storedPassword.startsWith("$2a$")) {

                    passwordValid =
                            BCrypt.checkpw(pass, storedPassword);

                } else {
                    // Support existing plain-text accounts temporarily
                    passwordValid =
                            storedPassword != null &&
                            storedPassword.equals(pass);

                    // Automatically upgrade password after successful login
                    if (passwordValid) {
                        String hashedPassword =
                                BCrypt.hashpw(pass, BCrypt.gensalt(12));

                        try (PreparedStatement update =
                                c.prepareStatement(
                                    "UPDATE EmployeeMaster " +
                                    "SET Password=? WHERE EmployeeCode=?")) {

                            update.setString(1, hashedPassword);
                            update.setInt(2, employeeCode);
                            update.executeUpdate();
                        }
                    }
                }

                if (!passwordValid) {
                    resp.sendRedirect("login.html?error=invalid");
                    return;
                }

                // Prevent session fixation
                HttpSession oldSession = req.getSession(false);

                if (oldSession != null) {
                    oldSession.invalidate();
                }

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

                resp.sendRedirect("raise_claim.html");
            }

        } catch (SQLException e) {
            throw new ServletException("Login/database error", e);
        }
    }
}
