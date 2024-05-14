package lakshyadesai_CSCI201L_Assignment4;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class PortfolioServlet
 */
@WebServlet("/PortfolioServlet")
public class PortfolioServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
		Optional<String> user_id = readCookie("user_id", request);
        if(user_id.isPresent()) {
        	int userId = Integer.valueOf(user_id.get());
        	List<portfolioInfo> stock = new ArrayList<>();
        	stock = getInfo(userId);
            double balance = getUserBalance(userId);
            
            PortfolioResponse portfolioResponse = new PortfolioResponse(stock, balance);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(portfolioResponse);
            
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(jsonResponse);
            out.flush(); 
        }
        
	}
	
	public portfolioInfo fetchCurrentStock(portfolioInfo p) throws IOException, InterruptedException {
		String finnhubApiKey = "cnrp4l9r01qqp8d0ir10cnrp4l9r01qqp8d0ir1g";
		
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://finnhub.io/api/v1/quote?symbol=" + p.getTicker() + "&token=" + finnhubApiKey))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        double currentPrice = jsonResponse.get("c").getAsDouble();
        double change = jsonResponse.get("d").getAsDouble();
        p.setCurrentPrice(currentPrice);
        p.setChange(change);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("https://finnhub.io/api/v1/stock/profile2?symbol=" + p.getTicker() + "&token=" + finnhubApiKey))
                .GET()
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonResponse2 = JsonParser.parseString(response2.body()).getAsJsonObject();
        String companyName = jsonResponse2.get("name").getAsString();
        p.setCompanyName(companyName);
        return p;
    }
	
	public Optional<String> readCookie(String key, HttpServletRequest request) {
	    return Arrays.stream(request.getCookies())
	      .filter(c -> key.equals(c.getName()))
	      .map(Cookie::getValue)
	      .findAny();
	}
	
	public List<portfolioInfo> getInfo(int user_id) {
		
		Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<portfolioInfo> allStocks = new ArrayList<>();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
	        conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=S0cc3r108!");
	        String query = "SELECT ticker, sum(numStock) as total_qty, sum(numStock*price) as total, sum(numStock*price) / sum(NumStock) as average "
	        		+ "from portfolio WHERE user_id = ? group by ticker";
	        pst = conn.prepareStatement(query);
	        pst.setInt(1, user_id);
			rs = pst.executeQuery();
			while(rs.next()) {
				String ticker = rs.getString(1);
				int total_qty = rs.getInt(2);
				if(total_qty == 0) {
					String q = "DELETE FROM Portfolio WHERE ticker = ? AND user_id = ?";
					pst = conn.prepareStatement(q);
					pst.setString(1, ticker);
					pst.setInt(2, user_id);
					rs = pst.executeQuery();
					
				} else {
				double total_cost = rs.getDouble(3);
				double average = rs.getDouble(4);
				portfolioInfo p = new portfolioInfo(ticker, total_qty, total_cost);
				p.setAverage(average);
				p = fetchCurrentStock(p);
				p.setMarketValue(p.getCurrentPrice()*p.getTotal_qty());
            	allStocks.add(p);
				}
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return allStocks;
	}
	
	public double getUserBalance(int user_id) {
		
		Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=S0cc3r108!");
			pst = conn.prepareStatement("SELECT balance FROM userinfo WHERE user_id = ?");
	        pst.setInt(1, user_id);
	        rs = pst.executeQuery();
        	if (rs.next()) {
                return rs.getDouble("balance");
            }
		} catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return 0.0;
        
	}

}

class PortfolioResponse {
    private List<portfolioInfo> stocks;
    private double balance;

    public PortfolioResponse(List<portfolioInfo> stocks, double balance) {
        this.stocks = stocks;
        this.balance = balance;
    }

    public List<portfolioInfo> getStocks() {
		return stocks;
	}

	public void setStocks(List<portfolioInfo> stocks) {
		this.stocks = stocks;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}

class portfolioInfo {
	private String ticker;
	private String companyName;
	private int total_qty;
	private double totalCost;
	private double currentPrice;
	private double change;
	private double marketValue;
	private double average;
	
	public portfolioInfo(String ticker, int total_qty, double totalCost) {
		super();
		this.ticker = ticker;
		this.total_qty = total_qty;
		this.totalCost = totalCost;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getTotal_qty() {
		return total_qty;
	}

	public void setTotal_qty(int total_qty) {
		this.total_qty = total_qty;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public double getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(double marketValue) {
		this.marketValue = marketValue;
	}
	
}