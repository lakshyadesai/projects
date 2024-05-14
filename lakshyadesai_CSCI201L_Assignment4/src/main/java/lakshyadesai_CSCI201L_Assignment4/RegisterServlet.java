package lakshyadesai_CSCI201L_Assignment4;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.regex.*;
import com.google.gson.Gson;


@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter pw = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
//        User user = new Gson().fromJson(request.getReader(), User.class);

        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        Gson gson = new Gson();

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
        	String error = "Passwords don't match";
        	pw.write(gson.toJson(error));
            pw.flush();
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
        	String error = "Invalid email";
        	pw.write(gson.toJson(error));
            pw.flush();
            return;
        }

        int user_id = registerUser(username, password, email);
        if (user_id > 0) {
        	String user_id_string_value = Integer.toString(user_id);
        	Cookie c = new Cookie("user_id", user_id_string_value); 
            response.addCookie(c);
            c.setMaxAge(-1);
        	response.sendRedirect("index.html?status=success"); // Redirect to login page on successful registration
             
        } else {
            response.sendRedirect("login.html?status=error");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    private int registerUser(String username, String password, String email) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=S0cc3r108!");

            // Check if user already exist
            pst = conn.prepareStatement("SELECT username FROM userinfo WHERE username = ?");
            pst.setString(1, username);
            rs = pst.executeQuery();
            if (rs.next()) {
                return -1; // User already exists
            }

            // Insert new user
            pst = conn.prepareStatement("INSERT INTO userinfo(username, password, email, balance) VALUES (" + "\"" + username + "\"," + "\"" +password + "\",\"" + email + "\"," + 50000.0 + ")");
            pst.executeUpdate();
            
            rs = pst.executeQuery("SELECT LAST_INSERT_ID()");
            rs.next();
            int user_id = rs.getInt(1);
            return user_id;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return -1;
    }
}

//package lakshyadesai_CSCI201L_Assignment4;
//
//import java.io.IOException;
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import com.google.gson.Gson;
//import java.io.*;
//import javax.servlet.*;
//import javax.servlet.http.*;
//import java.sql.*;
//import java.util.regex.*;
//
//@WebServlet("/RegisterServlet")
//public class RegisterServlet extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        PrintWriter pw = response.getWriter();
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//
//        User user = new Gson().fromJson(request.getReader(), User.class);
//
//        String email = request.getParameter("email");
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//        String confirmPassword = request.getParameter("confirmPassword");
//
//        Gson gson = new Gson();
//
//        if (username == null || username.isBlank()
//            || password == null || password.isBlank()
//            || email == null || email.isBlank()
//            || confirmPassword == null || confirmPassword.isBlank()
//            ) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            String error = "User info missing";
//            pw.write(gson.toJson(error));
//            pw.flush();
//        }
//        
//        if(!isValidEmail(email)) {
//        	String error = "Invalid email";
//        	pw.write(gson.toJson(error));
//            pw.flush();
//        }
//        
//        if(!password.equals(confirmPassword)) {
//        	String error = "Passwords don't match";
//        	pw.write(gson.toJson(error));
//            pw.flush();
//        }
//
//        int userID = registerUser(username, password, email);
//
//        if (userID == -1) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            String error = "Username is taken";
//            pw.write(gson.toJson(error));
//            pw.flush();
//        } else {
//            response.setStatus(HttpServletResponse.SC_OK);
//            pw.write(gson.toJson(userID));
//            pw.flush();
//        }
//    }
//    
//    private boolean isValidEmail(String email) {
//        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
//        Pattern pattern = Pattern.compile(emailRegex);
//        if (email == null) {
//            return false;
//        }
//        return pattern.matcher(email).matches();
//    }
//
//    private int registerUser(String username, String password, String email) {
//        Connection conn = null;
//        PreparedStatement pst = null;
//        ResultSet rs = null;
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=S0cc3r108!");
//
//            // Check if user already exist
//            pst = conn.prepareStatement("SELECT username FROM userinfo WHERE username = ?");
//            pst.setString(1, username);
//            rs = pst.executeQuery();
//            if (rs.next()) {
//                return -1; // User already exists
//            }
//
//            // Insert new user
//            pst = conn.prepareStatement("INSERT INTO userinfo(username, password, email, balance) values (" + "\"" + username + "\"," + "\"" + password + "\"," + "\"" + email + "\"," + 50000.0 + ")");
//            pst.executeUpdate();
//            
//            rs = pst.executeQuery("SELECT LAST_INSERT_ID()");
//            rs.next();
//            int user_id = rs.getInt(1);
//            return user_id;
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (rs != null) rs.close();
//                if (pst != null) pst.close();
//                if (conn != null) conn.close();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//        return -1;
//    }
//}

