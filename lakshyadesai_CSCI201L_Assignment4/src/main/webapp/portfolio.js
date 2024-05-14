document.addEventListener('DOMContentLoaded', function() {
    updateNavbarOnLoad();
    getPortfolio();
});

function updateNavbarOnLoad() {
    var user_id = getCookie("user_id");
    if(user_id > 0) {
		console.log("user_id > 0: user_id = " + user_id);
		updateNavbar(true);
	} else {
		console.log("user_id < 0: user_id = " + user_id);
		updateNavbar(false);
	}
}

function getCookie(cname) {
  let name = cname + "=";
  let ca = document.cookie.split(';');
  for(let i = 0; i < ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function delete_cookie( name, path, domain ) {
  if( getCookie( name ) ) {
    document.cookie = name + "=" +
      ((path) ? ";path="+path:"")+
      ((domain)?";domain="+domain:"") +
      ";expires=Thu, 01 Jan 1970 00:00:01 GMT";
  }
}

function updateNavbar(isLoggedIn) {
    const linksContainer = document.querySelector('.navbar-links');
    if (isLoggedIn) {
        linksContainer.innerHTML = `
            <a href="index.html">Home/Search</a>
            <a href="portfolio.html">Portfolio</a>
            <a onclick="logout(); event.preventDefault();">Logout</a>
        `;
    } else {
        linksContainer.innerHTML = `
            <a href="index.html">Home/Search</a>
            <a href="login.html">Login / Sign Up</a>
        `;
    }
}

function logout() {
	console.log("logging out");
	delete_cookie("user_id", "/lakshyadesai_CSCI201L_Assignment4", "localhost");
	console.log("cookie user_id = " + getCookie("user_id"));
    updateNavbar(false);
    window.location.href = 'index.html'; 
}

function getPortfolio() {
    const user_id = getCookie("user_id");
    if (!user_id) {
        alert('User is not logged in');
        return;
    }
    
    fetch('PortfolioServlet', {
        method: 'GET',
    })
    .then(response => response.json())
    .then(data => {
		const portfolioData = data.stocks;
        const userCashBalance = data.balance;
        displayPortfolio(portfolioData, userCashBalance);
    })
    .catch(error => {
        console.error('Error fetching portfolio:', error);
    });
}

function displayPortfolio(portfolioData, cashBalance) {
    const stockListDiv = document.getElementById('stock-list');
    stockListDiv.innerHTML = '';

    if (!portfolioData.length) {
        alert('No stocks in the portfolio.');
    }
    
    const cashBalanceSpan = document.getElementById('cashBalance');
    const totalAccountValueSpan = document.getElementById('totalAccountValue');
    
    let totalAccountValue = cashBalance;

    portfolioData.forEach(stock => {
		totalAccountValue += stock.currentPrice * stock.total_qty;
        const stockDiv = document.createElement('div');
        stockDiv.className = 'stock-item';
        
        
        const changeClass = stock.change >= 0 ? 'positive-change' : 'negative-change';
        const changeArrow = stock.change >= 0 ? "<span class='icon-up'></span>" : "<span class='icon-down'></span>";
        
        stockDiv.innerHTML = `
            <h3>${stock.ticker} - ${stock.companyName}</h3>
            <div class="card">
	            <div class="info"><label>Quantity:</label> <span>${stock.total_qty}</span></div>
	            <div class="info"><label>Change:</label> <span class="info ${changeClass}">${changeArrow} ${stock.change.toFixed(2)}</span></div>
	            <div class="info"><label>Avg. Cost / Share:</label> <span>$${stock.totalCost.toFixed(2)}</span></div>
	            <div class="info"><label>Current Price:</label> <span>$${stock.currentPrice.toFixed(2)}</span></div>
	            <div class="info"><label>Total Cost:</label> <span>$${stock.average.toFixed(2)}</span></div>
	            <div class="info"><label>Market Value:</label> <span>$${stock.marketValue.toFixed(2)}</span></div>
	            
            </div>
            <div class="transaction-form">
                <div class="qty">Quantity: <input type="number" id="quantity-${stock.ticker}" style="width: 40px; height: 20px"></div>
                <div class="options"><label><input type="radio" name="transactionType-${stock.ticker}" value="BUY"> BUY</label>
                <label><input type="radio" name="transactionType-${stock.ticker}" value="SELL"> SELL</label></div>
                <div class="submitButton"><button onclick="submitTrade('${stock.ticker}', ${stock.currentPrice}, ${stock.total_qty})">Submit</button></div>
            </div>
            
        `;
        stockListDiv.appendChild(stockDiv);
    });
    
    cashBalanceSpan.textContent = cashBalance.toFixed(2);
    totalAccountValueSpan.textContent = totalAccountValue.toFixed(2);

}
    
function submitTrade(ticker, currentPrice, total_qty) {
    const quantityField = document.getElementById(`quantity-${ticker}`);
    const quantity = quantityField.value;
    if (!quantity || quantity < 0) {
        alert('Please enter a quantity.');
        return;
    }

    const transactionTypeElement = document.querySelector(`input[name="transactionType-${ticker}"]:checked`);
	const transactionType = transactionTypeElement ? transactionTypeElement.value : null;
    if (!transactionType) {
        alert('Please select a transaction type.');
        return;
    }
    if(transactionType === "SELL" && (quantity > total_qty)) {
		alert('Please provide an appropariate quantity.');
	}

    const user_id = getCookie("user_id");

    const tradeDetails = {
        ticker: ticker,
        numStock: parseInt(quantity),
        price: parseFloat(currentPrice),
        tradeAction: transactionType,
        user_id: parseInt(user_id)
    };
    fetch('TradeServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(tradeDetails)
    })
    .then(response => response.json())
    .then(data => {
	    if (data.success) {
	        const totalCost = tradeDetails.numStock * tradeDetails.price;
	        if(tradeDetails.tradeAction === "BUY") {
				var action = "Bought";
			} else {
				var action = "Sold"
			}
	        alert(`${action} ${tradeDetails.numStock} shares of ${tradeDetails.ticker} for $${totalCost.toFixed(2)}`);
	        getPortfolio();
	    } else {
	        alert(data.message);
	    }
	}).catch(error => {
        console.error('Error fetching portfolio:', error);
    });
}