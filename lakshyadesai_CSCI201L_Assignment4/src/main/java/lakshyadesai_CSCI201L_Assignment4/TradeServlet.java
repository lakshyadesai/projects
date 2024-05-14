package lakshyadesai_CSCI201L_Assignment4;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/TradeServlet")
public class TradeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        

        Trade trade = gson.fromJson(request.getReader(), Trade.class);
        
   
        boolean tradeSuccess = false;
        if (trade.getTradeAction().equalsIgnoreCase("BUY") || trade.getTradeAction().equalsIgnoreCase("SELL")) {
        	tradeSuccess = executeTrade(trade, out, gson);
        } else {
        	tradeSuccess = false;
            sendError(out, gson, "Invalid trade action. Only 'BUY' or 'SELL' is allowed.");
        }
        

        if (tradeSuccess) {
            out.println(gson.toJson(new TradeResponse(true, "Stock purchased successfully")));
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(gson.toJson(new TradeResponse(false, "Failed to purchase stock")));
        }
        out.flush();
    }
    
    private String getCurrentDate() {

        return java.time.LocalDate.now().toString();
    }

    private boolean executeTrade(Trade trade, PrintWriter out, Gson gson) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=S0cc3r108!");

  
            if (trade.getNumStock() < 1) {
            	sendError(out, gson, "Invalid quantity. Must be greater than zero.");
                return false;
            }


            double tradeCost = trade.getNumStock() * trade.getPrice();
            double userBalance = getUserBalance(conn, trade.getUserId());
            if (userBalance < tradeCost) {
            	sendError(out, gson, "Insufficient funds to complete the purchase.");
                return false;
            }

            int num = trade.getNumStock();
            String updateBalanceSQL = "UPDATE userinfo SET balance = balance - ? WHERE user_id = ?";
            if(trade.getTradeAction().equals("SELL")) {
            	if(!hasEnoughStocks(conn, trade.getUserId(), trade.getTicker(), trade.getNumStock())) {
                	sendError(out, gson, "Not Enough stocks available.");
                	return false;
                }
            	num = -1*trade.getNumStock();
            	updateBalanceSQL = "UPDATE userinfo SET balance = balance + ? WHERE user_id = ?";
            } 
        

            String insertTradeSQL = "INSERT INTO Portfolio (user_id, ticker, numStock, price) VALUES (?, ?, ?, ?)";
            pst = conn.prepareStatement(insertTradeSQL);
            pst.setInt(1, trade.getUserId());
            pst.setString(2, trade.getTicker());
            pst.setInt(3, num);
            pst.setDouble(4, trade.getPrice());
            int affectedRows = pst.executeUpdate();

 
            pst = conn.prepareStatement(updateBalanceSQL);
            pst.setDouble(1, tradeCost);
            pst.setInt(2, trade.getUserId());
            pst.executeUpdate();

            return affectedRows > 0;
        } catch (Exception e) {
        	sendError(out, gson, "An error occurred while executing the trade.");
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean hasEnoughStocks(Connection conn, int userId, String ticker, int requiredNumStock) throws SQLException {
        String query = "SELECT sum(numStock) as numStock FROM Portfolio WHERE user_id = ? AND ticker = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            pst.setString(2, ticker);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next() && rs.getInt("numStock") >= requiredNumStock) {
                    return true;
                }
            }
        }
        return false;
    }


    private double getUserBalance(Connection conn, int userId) throws SQLException {
        String query = "SELECT balance FROM userinfo WHERE user_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }
        return 0;
    }
    
    private void sendError(PrintWriter out, Gson gson, String errorMessage) {
        TradeResponse response = new TradeResponse(false, errorMessage);
        out.println(gson.toJson(response));
        out.flush();
    }

    static class TradeResponse {
        boolean success;
        String message;

        TradeResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
