package com.punit.reimbursement.servlet;
import com.punit.reimbursement.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;import java.sql.*;
@WebServlet(urlPatterns={"/api/pending-claims","/api/update-claim"}) public class ApprovalServlet extends HttpServlet {
 private boolean manager(HttpSession s){return s!=null&&"Manager".equalsIgnoreCase((String)s.getAttribute("designation"));}
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws IOException{
  HttpSession s=q.getSession(false);if(!manager(s)){p.sendError(403);return;}
  String sql="SELECT c.CM_ClaimID,c.CM_EmployeeCode,em.Name,c.CM_SubmittedOn,e.ExpenseDescription,c.CM_Amount,c.CM_Description FROM ClaimMaster c JOIN EmployeeMaster em ON c.CM_EmployeeCode=em.EmployeeCode LEFT JOIN ExpenseMaster e ON c.CM_ExpenseCode=e.ExpenseCode WHERE c.CM_Status=2 AND c.CMRepMgrCode=? ORDER BY c.CM_ClaimID DESC";
  p.setContentType("application/json;charset=UTF-8");try(Connection c=DBConnection.getConnection();PreparedStatement x=c.prepareStatement(sql)){x.setInt(1,(Integer)s.getAttribute("employeeCode"));try(ResultSet r=x.executeQuery()){StringBuilder b=new StringBuilder("[");boolean f=true;while(r.next()){if(!f)b.append(",");b.append("{\"id\":").append(r.getInt(1)).append(",\"employeeCode\":").append(r.getInt(2)).append(",\"employeeName\":\"").append(Json.escape(r.getString(3))).append("\",\"date\":\"").append(Json.escape(String.valueOf(r.getTimestamp(4)))).append("\",\"expense\":\"").append(Json.escape(r.getString(5))).append("\",\"amount\":").append(r.getBigDecimal(6)).append(",\"description\":\"").append(Json.escape(r.getString(7))).append("\"}");f=false;}p.getWriter().write(b.append("]").toString());}}
  catch(SQLException e){throw new IOException(e);}
 }
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws IOException{
  HttpSession s=q.getSession(false);if(!manager(s)){p.sendError(403);return;}try{int id=Integer.parseInt(q.getParameter("claimId")),status=Integer.parseInt(q.getParameter("status"));if(status<3||status>5){p.sendError(400);return;}
   String sql="UPDATE ClaimMaster SET CM_Status=? WHERE CM_ClaimID=? AND CMRepMgrCode=? AND CM_Status=2";
   try(Connection c=DBConnection.getConnection();PreparedStatement x=c.prepareStatement(sql)){x.setInt(1,status);x.setInt(2,id);x.setInt(3,(Integer)s.getAttribute("employeeCode"));if(x.executeUpdate()==0){p.sendError(404);return;}}
   p.sendRedirect(q.getContextPath()+"/approve_claims.html?updated=1");
  }catch(NumberFormatException e){p.sendError(400);}catch(SQLException e){throw new IOException(e);}
 }
}