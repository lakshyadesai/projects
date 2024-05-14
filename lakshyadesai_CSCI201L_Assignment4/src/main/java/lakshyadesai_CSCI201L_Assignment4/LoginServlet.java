package lakshyadesai_CSCI201L_Assignment4;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import com.google.gson.Gson;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Get username and password from request
        String username = request.getParameter("login_username");
        String password = request.getParameter("login_password");

        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        int user_id = validate(username, password);
        if (user_id > 0) {
            // Setting up the session
            HttpSession session = request.getSession();
            session.setAttribute("user", username);
            session.setMaxInactiveInterval(30*60); // session expires in 30 minutes

            // Redirect to the main page
            String user_id_string_value = Integer.toString(user_id);
        	Cookie c = new Cookie("user_id", user_id_string_value); 
            response.addCookie(c);
            c.setMaxAge(-1);
            response.sendRedirect("index.html");
        } else {
            // Login failed
            out.println("<html><body>");
            out.println("<h1>Invalid Username or Password</h1>");
            out.println("Click here to <a href='login.html'>try again</a>");
            out.println("</body></html>");
        }
    }

    private int validate(String user, String pass) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=S0cc3r108!");
            pst = conn.prepareStatement("SELECT user_id FROM userinfo WHERE username= \"" + user + "\" AND " + "password= \"" + pass + "\"");

            rs = pst.executeQuery();
            if(rs.next()) {
            	return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) { rs.close(); }
                if (pst != null) { pst.close(); }
                if (conn != null) { conn.close(); }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}

