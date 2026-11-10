package com.punit.reimbursement.servlet;
import com.punit.reimbursement.DBConnection;import jakarta.servlet.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;
import java.io.IOException;import java.math.BigDecimal;import java.sql.*;
@WebServlet("/submit-claim") public class ClaimServlet extends HttpServlet {
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  HttpSession s=q.getSession(false);if(s==null||s.getAttribute("employeeCode")==null){p.sendRedirect("login.html");return;}
  try{
   int emp=(Integer)s.getAttribute("employeeCode"),exp=Integer.parseInt(q.getParameter("expense_code"));String contact=q.getParameter("contact"),desc=q.getParameter("description");BigDecimal amount=new BigDecimal(q.getParameter("amount"));
   if(amount.signum()<=0||contact==null||contact.isBlank()||desc==null||desc.isBlank()){p.sendRedirect("raise_claim.html?error=validation");return;}
   Integer mgr=(Integer)s.getAttribute("reportingManagerCode");String sql="INSERT INTO ClaimMaster(CM_EmployeeCode,CM_Status,CM_ContactNo,CMRepMgrCode,CM_ExpenseCode,CM_Amount,CM_Description) VALUES(?,2,?,?,?,?,?)";
   try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql)){st.setInt(1,emp);st.setString(2,contact);if(mgr==null)st.setNull(3,Types.INTEGER);else st.setInt(3,mgr);st.setInt(4,exp);st.setBigDecimal(5,amount);st.setString(6,desc);st.executeUpdate();}
   p.sendRedirect("raise_claim.html?success=1");
  }catch(NumberFormatException|ArithmeticException e){p.sendRedirect("raise_claim.html?error=validation");}catch(SQLException e){throw new ServletException("Could not save claim",e);}
 }
}