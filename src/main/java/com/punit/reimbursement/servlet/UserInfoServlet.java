package com.punit.reimbursement.servlet;
import com.punit.reimbursement.Json;import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/api/user") public class UserInfoServlet extends HttpServlet {
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws IOException{
  HttpSession s=q.getSession(false);if(s==null||s.getAttribute("employeeCode")==null){p.sendError(401);return;}
  p.setContentType("application/json;charset=UTF-8");
  p.getWriter().printf("{\"employeeCode\":%s,\"name\":\"%s\",\"designation\":\"%s\",\"contactNumber\":\"%s\"}",s.getAttribute("employeeCode"),Json.escape((String)s.getAttribute("employeeName")),Json.escape((String)s.getAttribute("designation")),Json.escape((String)s.getAttribute("contactNumber")));
 }
}