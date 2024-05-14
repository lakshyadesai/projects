# JoesStocks Trading Platform

## Project Overview

JoesStocks is a comprehensive web-based stock trading platform designed to enhance the efficiency and decision-making capabilities of users. It enables users to search for, analyze, and trade stocks in real time. The platform offers an intuitive user experience with interactive elements such as live stock data displays and user-specific dashboards.

## Features

- **Stock Search**: Users can search for stocks using ticker symbols to view real-time data and historical trends.
- **Interactive Dashboard**: Provides a personalized dashboard for users to track their investments and market movements.
- **Real-Time Trading**: Enables users to buy and sell stocks, updating their portfolio and balance instantly.
- **Secure Authentication**: Manages user sessions and authentication securely to protect sensitive financial data.

## Technologies Used

- **Frontend**: HTML, CSS, JavaScript
  - Dynamic updates using AJAX
  - Responsive web design for various devices
- **Backend**: Java Servlets
  - RESTful API integration for stock data using Finnhub API
  - Secure session management
- **Database**: MySQL
  - Structured schema for efficient data retrieval
  - Secure storage of user credentials and transaction records
- **Version Control**: Git
- **Development Methodology**: Agile development practices

## Getting Started

### Prerequisites

- Java JDK 8 or above
- Apache Tomcat Server 9.0 or above
- MySQL 8.0 or above
- Modern web browser such as Chrome, Firefox, or Edge

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/lakshyadesai/projects.git
   cd joesstocks
   ```

2. **Database Setup**
   - Create the database and user tables using the SQL script provided in `db/schema.sql`.
   - Ensure your database credentials are updated in your server's configuration files.

3. **Build and Run the Application**
   - Import the project into an IDE like Eclipse or IntelliJ and set up Tomcat Server.
   - Build the project to resolve dependencies.
   - Run the project on Tomcat Server.

4. **Access the Platform**
   - Open a web browser and go to `http://localhost:8080/index.html` to start trading.
