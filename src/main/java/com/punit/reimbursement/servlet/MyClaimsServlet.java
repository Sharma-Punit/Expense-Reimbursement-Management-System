package com.punit.reimbursement.servlet;
import com.punit.reimbursement.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;import java.sql.*;
@WebServlet("/api/my-claims") public class MyClaimsServlet extends HttpServlet {
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws IOException{
  HttpSession s=q.getSession(false);if(s==null||s.getAttribute("employeeCode")==null){p.sendError(401);return;}
  String sql="SELECT c.CM_ClaimID,c.CM_SubmittedOn,e.ExpenseDescription,c.CM_Amount,c.CM_Description,st.SM_StatusDescription FROM ClaimMaster c LEFT JOIN ExpenseMaster e ON c.CM_ExpenseCode=e.ExpenseCode LEFT JOIN StatusMaster st ON c.CM_Status=st.SM_StatusID WHERE c.CM_EmployeeCode=? ORDER BY c.CM_ClaimID DESC";
  p.setContentType("application/json;charset=UTF-8");try(Connection c=DBConnection.getConnection();PreparedStatement x=c.prepareStatement(sql)){x.setInt(1,(Integer)s.getAttribute("employeeCode"));try(ResultSet r=x.executeQuery()){StringBuilder b=new StringBuilder("[");boolean f=true;while(r.next()){if(!f)b.append(",");b.append("{\"id\":").append(r.getInt(1)).append(",\"date\":\"").append(Json.escape(String.valueOf(r.getTimestamp(2)))).append("\",\"expense\":\"").append(Json.escape(r.getString(3))).append("\",\"amount\":").append(r.getBigDecimal(4)).append(",\"description\":\"").append(Json.escape(r.getString(5))).append("\",\"status\":\"").append(Json.escape(r.getString(6))).append("\"}");f=false;}p.getWriter().write(b.append("]").toString());}}
  catch(SQLException e){throw new IOException(e);}
 }
}