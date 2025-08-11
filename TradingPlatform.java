import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// --- 1. Stock Class ---
// Represents a single stock in the market
class Stock implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization
    private String symbol;
    private String companyName;
    private double currentPrice;

    public Stock(String symbol, String companyName, double currentPrice) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public String toString() {
        return String.format("%s (%s): ₹%.2f", symbol, companyName, currentPrice);
    }
}

// --- 2. Transaction Class ---
// Records a buy or sell operation
class Transaction implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization

    public enum Type {
        BUY, SELL
    }

    private String stockSymbol;
    private Type type;
    private int quantity;
    private double pricePerShare;
    private LocalDateTime timestamp;

    public Transaction(String stockSymbol, Type type, int quantity, double pricePerShare) {
        this.stockSymbol = stockSymbol;
        this.type = type;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.timestamp = LocalDateTime.now(); // Record current time of transaction
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Type getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPricePerShare() {
        return pricePerShare;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("%s %d shares of %s at ₹%.2f/share on %s",
                             type, quantity, stockSymbol, pricePerShare, timestamp.format(formatter));
    }
}

// --- 3. Portfolio Class ---
// Manages user's cash, stock holdings, and transaction history
class Portfolio implements Serializable {
    private static final long serialVersionUID = 1L; // For serialization

    private double cashBalance;
    // Map to store stock holdings: Stock Symbol -> Quantity held
    private Map<String, Integer> holdings;
    private List<Transaction> transactionHistory;

    public Portfolio(double initialCash) {
        this.cashBalance = initialCash;
        this.holdings = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    /**
     * Attempts to buy shares of a given stock.
     * @param stock The Stock object to buy.
     * @param quantity The number of shares to purchase.
     * @return true if the purchase was successful, false otherwise.
     */
    public boolean buyStock(Stock stock, int quantity) {
        if (quantity <= 0) {
            System.out.println("❌ Invalid quantity. Must be greater than zero.");
            return false;
        }
        double cost = stock.getCurrentPrice() * quantity;
        if (cashBalance >= cost) {
            cashBalance -= cost; // Deduct cost from cash
            holdings.put(stock.getSymbol(), holdings.getOrDefault(stock.getSymbol(), 0) + quantity); // Add to holdings
            transactionHistory.add(new Transaction(stock.getSymbol(), Transaction.Type.BUY, quantity, stock.getCurrentPrice())); // Record transaction
            System.out.println(String.format("✅ Successfully bought %d shares of %s for ₹%.2f.", quantity, stock.getSymbol(), cost));
            return true;
        } else {
            System.out.println(String.format("❌ Insufficient cash. Need ₹%.2f, but only have ₹%.2f.", cost, cashBalance));
            return false;
        }
    }

    /**
     * Attempts to sell shares of a given stock.
     * @param stock The Stock object to sell.
     * @param quantity The number of shares to sell.
     * @return true if the sale was successful, false otherwise.
     */
    public boolean sellStock(Stock stock, int quantity) {
        if (quantity <= 0) {
            System.out.println("❌ Invalid quantity. Must be greater than zero.");
            return false;
        }
        String symbol = stock.getSymbol();
        // Check if user holds enough shares
        if (holdings.containsKey(symbol) && holdings.get(symbol) >= quantity) {
            double revenue = stock.getCurrentPrice() * quantity;
            cashBalance += revenue; // Add revenue to cash
            holdings.put(symbol, holdings.get(symbol) - quantity); // Deduct from holdings
            if (holdings.get(symbol) == 0) {
                holdings.remove(symbol); // Remove from holdings map if quantity becomes zero
            }
            transactionHistory.add(new Transaction(symbol, Transaction.Type.SELL, quantity, stock.getCurrentPrice())); // Record transaction
            System.out.println(String.format("✅ Successfully sold %d shares of %s for ₹%.2f.", quantity, symbol, revenue));
            return true;
        } else {
            System.out.println(String.format("❌ Insufficient shares of %s. You hold %d, but tried to sell %d.",
                                             symbol, holdings.getOrDefault(symbol, 0), quantity));
            return false;
        }
    }

    /**
     * Calculates the current total value of the portfolio (cash + value of all stock holdings).
     * @param marketData A map of current stock data (symbol -> Stock object).
     * @return The total current value of the portfolio.
     */
    public double getPortfolioValue(Map<String, Stock> marketData) {
        double stockValue = 0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            String symbol = entry.getKey();
            int quantity = entry.getValue();
            Stock stock = marketData.get(symbol); // Get current price from market data
            if (stock != null) {
                stockValue += stock.getCurrentPrice() * quantity;
            }
        }
        return cashBalance + stockValue;
    }

    /**
     * Displays the current cash balance and stock holdings.
     * @param marketData A map of current stock data to show real-time values.
     */
    public void displayPortfolio(Map<String, Stock> marketData) {
        System.out.println("\n--- Your Portfolio ---");
        System.out.println(String.format("Cash Balance: ₹%.2f", cashBalance));
        System.out.println("Holdings:");
        if (holdings.isEmpty()) {
            System.out.println("  No stocks held.");
        } else {
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                String symbol = entry.getKey();
                int quantity = entry.getValue();
                Stock stock = marketData.get(symbol);
                if (stock != null) {
                    System.out.println(String.format("  - %s (%s): %d shares (Current Price: ₹%.2f, Value: ₹%.2f)",
                                                     stock.getSymbol(), stock.getCompanyName(), quantity, stock.getCurrentPrice(),
                                                     stock.getCurrentPrice() * quantity));
                } else {
                    System.out.println(String.format("  - %s: %d shares (Price data unavailable)", symbol, quantity));
                }
            }
        }
        System.out.println(String.format("Total Portfolio Value: ₹%.2f", getPortfolioValue(marketData)));
        System.out.println("----------------------");
    }

    /**
     * Displays the chronological history of all buy and sell transactions.
     */
    public void displayTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        if (transactionHistory.isEmpty()) {
            System.out.println("  No transactions yet.");
        } else {
            // Display most recent transactions first
            for (int i = transactionHistory.size() - 1; i >= 0; i--) {
                System.out.println("  " + transactionHistory.get(i));
            }
        }
        System.out.println("---------------------------");
    }
}

// --- 4. Main TradingPlatform Class ---
// Manages the overall application flow and user interaction
public class TradingPlatform {

    // File name for persisting portfolio data
    private static final String PORTFOLIO_FILE = "portfolio.ser";
    private Portfolio userPortfolio;
    // Map to store current market data: Stock Symbol -> Stock Object
    private Map<String, Stock> marketData;

    private Scanner scanner; // For reading user input from console

    public TradingPlatform() {
        scanner = new Scanner(System.in);
        marketData = new HashMap<>();
        initializeMarketData(); // Setup available stocks
        userPortfolio = loadPortfolio(); // Attempt to load saved portfolio
        if (userPortfolio == null) {
            userPortfolio = new Portfolio(10000.00); // Create new portfolio with initial cash if no saved data
            System.out.println("🎉 Welcome! New portfolio created with initial cash: ₹" + userPortfolio.getCashBalance());
        } else {
            System.out.println("🚀 Portfolio loaded successfully! Current cash: ₹" + userPortfolio.getCashBalance());
        }
    }

    // Predefine some stocks and their initial prices
    private void initializeMarketData() {
        marketData.put("TCS", new Stock("TCS", "Tata Consultancy Services", 3800.00));
        marketData.put("RELIANCE", new Stock("RELIANCE", "Reliance Industries", 2950.00));
        marketData.put("HDFC", new Stock("HDFC", "HDFC Bank", 1500.00));
        marketData.put("INFY", new Stock("INFY", "Infosys", 1600.00));
        marketData.put("SBIN", new Stock("SBIN", "State Bank of India", 750.00));
    }

    // --- Persistence Methods ---

    /**
     * Saves the current user portfolio to a file using object serialization.
     */
    private void savePortfolio() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PORTFOLIO_FILE))) {
            oos.writeObject(userPortfolio);
            System.out.println("💾 Portfolio saved successfully to " + PORTFOLIO_FILE);
        } catch (IOException e) {
            System.err.println("🚫 Error saving portfolio: " + e.getMessage());
        }
    }

    /**
     * Loads a portfolio from a file using object deserialization.
     * @return The loaded Portfolio object, or null if no file or an error occurs.
     */
    private Portfolio loadPortfolio() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PORTFOLIO_FILE))) {
            return (Portfolio) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("ℹ️ No existing portfolio found. A new one will be created.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("🚫 Error loading portfolio: " + e.getMessage());
            return null;
        }
    }

    // --- Trading Operations (UI Layer) ---

    /**
     * Displays all available stocks and their current prices.
     */
    public void displayMarketData() {
        System.out.println("\n--- Current Market Data ---");
        if (marketData.isEmpty()) {
            System.out.println("No stocks available in the market.");
            return;
        }
        for (Stock stock : marketData.values()) {
            System.out.println(stock);
        }
        System.out.println("---------------------------");
    }

    /**
     * Guides the user through the process of buying a stock.
     */
    public void buyStock() {
        System.out.print("Enter stock symbol to buy (e.g., TCS, RELIANCE): ");
        String symbol = scanner.next().toUpperCase(); // Read input and convert to uppercase for lookup
        Stock stockToBuy = marketData.get(symbol);

        if (stockToBuy == null) {
            System.out.println("Stock with symbol '" + symbol + "' not found in market data.");
            return;
        }

        System.out.print(String.format("Enter quantity for %s (Current Price: ₹%.2f): ", symbol, stockToBuy.getCurrentPrice()));
        try {
            int quantity = scanner.nextInt();
            userPortfolio.buyStock(stockToBuy, quantity); // Delegate to portfolio logic
        } catch (InputMismatchException e) {
            System.out.println("Invalid quantity. Please enter a whole number.");
            scanner.next(); // Consume the invalid input to prevent infinite loop
        }
    }

    /**
     * Guides the user through the process of selling a stock.
     */
    public void sellStock() {
        userPortfolio.displayPortfolio(marketData); // Show holdings before asking to sell
        System.out.print("Enter stock symbol to sell (e.g., TCS, RELIANCE): ");
        String symbol = scanner.next().toUpperCase();
        Stock stockToSell = marketData.get(symbol);

        if (stockToSell == null) {
            System.out.println("Stock with symbol '" + symbol + "' not found in market data.");
            return;
        }

        // Pre-check if user owns any shares of this stock
        if (!userPortfolio.getHoldings().containsKey(symbol) || userPortfolio.getHoldings().get(symbol) == 0) {
            System.out.println("You do not own any shares of " + symbol + " to sell.");
            return;
        }

        System.out.print(String.format("Enter quantity for %s (You own %d shares): ", symbol, userPortfolio.getHoldings().get(symbol)));
        try {
            int quantity = scanner.nextInt();
            userPortfolio.sellStock(stockToSell, quantity); // Delegate to portfolio logic
        } catch (InputMismatchException e) {
            System.out.println("Invalid quantity. Please enter a whole number.");
            scanner.next(); // Consume the invalid input
        }
    }

    // --- Main Application Loop ---

    /**
     * Starts the main trading platform loop, displaying menu and processing user choices.
     */
    public void start() {
        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        displayMarketData();
                        break;
                    case 2:
                        buyStock();
                        break;
                    case 3:
                        sellStock();
                        break;
                    case 4:
                        userPortfolio.displayPortfolio(marketData);
                        break;
                    case 5:
                        userPortfolio.displayTransactionHistory();
                        break;
                    case 6:
                        savePortfolio();
                        break;
                    case 7:
                        System.out.println("👋 Exiting platform. Goodbye!");
                        break;
                    default:
                        System.out.println("🤷 Invalid choice. Please enter a number from the menu.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a number (1-7).");
                scanner.next(); // Consume the invalid input to avoid infinite loop
                choice = 0; // Set to a value that keeps the loop running
            }
            System.out.println("\n"); // Add a newline for better readability between actions
        } while (choice != 7);
        scanner.close(); // Close the scanner to release resources
    }

    // Displays the main menu options to the user
    private void displayMenu() {
        System.out.println("--- Stock Trading Platform ---");
        System.out.println("1. View Market Data");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio");
        System.out.println("5. View Transaction History");
        System.out.println("6. Save Portfolio");
        System.out.println("7. Exit");
        System.out.println("-----------------------------");
    }

    // Main method to run the application
    public static void main(String[] args) {
        TradingPlatform platform = new TradingPlatform();
        platform.start(); // Start the trading simulation
    }
}