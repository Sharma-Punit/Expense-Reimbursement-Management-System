CREATE DATABASE IF NOT EXISTS reimbursement;
USE reimbursement;
CREATE TABLE IF NOT EXISTS EmployeeMaster(EmployeeCode INT PRIMARY KEY,Password VARCHAR(255),Name VARCHAR(100),Designation VARCHAR(100),EmailId VARCHAR(100),Gender VARCHAR(10),DateOfJoining DATE,ContactNumber VARCHAR(20),ReportingManagerCode INT);
CREATE TABLE IF NOT EXISTS ExpenseMaster(ExpenseCode INT PRIMARY KEY,ExpenseDescription VARCHAR(255));
CREATE TABLE IF NOT EXISTS StatusMaster(SM_StatusID INT PRIMARY KEY,SM_StatusDescription VARCHAR(50));
CREATE TABLE IF NOT EXISTS ClaimMaster(CM_ClaimID INT AUTO_INCREMENT PRIMARY KEY,CM_EmployeeCode INT,CM_Status INT DEFAULT 2,CM_ContactNo VARCHAR(20),CMRepMgrCode INT,CM_SubmittedOn DATETIME DEFAULT CURRENT_TIMESTAMP,CM_ExpenseCode INT,CM_Amount DECIMAL(10,2),CM_Description TEXT,FOREIGN KEY(CM_EmployeeCode) REFERENCES EmployeeMaster(EmployeeCode),FOREIGN KEY(CM_ExpenseCode) REFERENCES ExpenseMaster(ExpenseCode));
INSERT IGNORE INTO StatusMaster VALUES(1,'New'),(2,'Submitted'),(3,'Approved'),(4,'Rejected'),(5,'Refer back');
INSERT IGNORE INTO ExpenseMaster VALUES(100,'Mobile Bill'),(101,'Taxi charges'),(102,'Canteen bills'),(103,'Miscellaneous'),(104,'Team Lunch'),(105,'Relocation expense');
INSERT IGNORE INTO EmployeeMaster VALUES(1001,'1234','Initiator User','Engineer','init@example.com','M','2024-04-01','9000000000',1002),(1002,'1234','Approver User','Manager','approver@example.com','F','2020-01-15','9000000001',NULL);
