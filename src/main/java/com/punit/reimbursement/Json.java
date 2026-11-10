package com.punit.reimbursement;
public final class Json {
 private Json(){}
 public static String escape(String s){if(s==null)return "";return s.replace("\\","\\\\").replace("\"","\\\"").replace("\r","\\r").replace("\n","\\n").replace("\t","\\t");}
}