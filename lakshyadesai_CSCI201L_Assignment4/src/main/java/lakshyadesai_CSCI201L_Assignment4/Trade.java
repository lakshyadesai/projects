package lakshyadesai_CSCI201L_Assignment4;

public class Trade {
    private String ticker; // Stock ticker symbol
    private int numStock; // Number of stocks involved in the trade
    private double price; // Price per stock for the trade
    private String tradeAction; // Type of trade, e.g., "BUY" or "SELL"
    private double totalValue; // Total value of the trade
    private int user_id;

    // Constructor
    public Trade(String ticker, int numStock, double price, String tradeAction, int user_id) {
        this.ticker = ticker;
        this.numStock = numStock;
        this.price = price;
        this.tradeAction = tradeAction;
        this.totalValue = numStock * price;
        this.user_id = user_id;
    }
    
    public int getUserId() {
    	return user_id;
    }
    
    public void set(int user_id) {
    	this.user_id = user_id;
    }

    // Getters and setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getNumStock() {
        return numStock;
    }

    public void setNumStock(int numStock) {
        this.numStock = numStock;
        this.totalValue = this.numStock * this.price; 
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.totalValue = this.numStock * this.price;
    }

    public String getTradeAction() {
        return tradeAction;
    }

    public void setTradeAction(String tradeAction) {
        this.tradeAction = tradeAction;
    }

    public double getTotalValue() {
        return totalValue;
    }

}

