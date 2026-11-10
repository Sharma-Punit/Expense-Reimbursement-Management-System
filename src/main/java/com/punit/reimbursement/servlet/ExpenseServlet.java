package com.punit.reimbursement.servlet;
import com.punit.reimbursement.*;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;import java.sql.*;
@WebServlet("/api/expenses") public class ExpenseServlet extends HttpServlet {
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws IOException{
  HttpSession s=q.getSession(false);if(s==null||s.getAttribute("employeeCode")==null){p.sendError(401);return;}
  p.setContentType("application/json;charset=UTF-8");String sql="SELECT ExpenseCode,ExpenseDescription FROM ExpenseMaster ORDER BY ExpenseCode";
  try(Connection c=DBConnection.getConnection();PreparedStatement st=c.prepareStatement(sql);ResultSet r=st.executeQuery()){
   StringBuilder b=new StringBuilder("[");boolean first=true;while(r.next()){if(!first)b.append(",");b.append("{\"code\":").append(r.getInt(1)).append(",\"description\":\"").append(Json.escape(r.getString(2))).append("\"}");first=false;}p.getWriter().write(b.append("]").toString());
  }catch(SQLException e){throw new IOException(e);}
 }
}