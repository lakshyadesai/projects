document.addEventListener('DOMContentLoaded', function() {
    updateNavbarOnLoad();
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

document.getElementById('searchButton').addEventListener('click', function() {
    var ticker = document.getElementById('searchField').value;
    validateTicker(ticker)
});

let currentTicker = '';

function validateTicker(ticker) {
    Promise.all([
        fetchCompanyProfile(ticker),
        fetchStockData(ticker),
        fetchMarketStatus('US')
    ]).then(([profileData, quoteData, marketData]) => {
        if (profileData && profileData.name && quoteData) {
            displayQuoteData(quoteData, profileData, ticker, marketData);
            displayProfileData(profileData);
            showOnlyMainContent();
        } else {
            alert('Invalid ticker symbol. Please try again.');
        }
    }).catch(error => {
        console.error('Error validating ticker:', error);
        alert('Failed to verify ticker symbol due to a network error.');
    });
}

function fetchStockData(ticker) {
    var apiKey = 'cnrp4l9r01qqp8d0ir10cnrp4l9r01qqp8d0ir1g'; 
    var quoteUrl = `https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${apiKey}`;
    return fetch(quoteUrl).then(response => response.json());
}

function fetchCompanyProfile(ticker) {
    var apiKey = 'cnrp4l9r01qqp8d0ir10cnrp4l9r01qqp8d0ir1g'; 
    var profileUrl = `https://finnhub.io/api/v1/stock/profile2?symbol=${ticker}&token=${apiKey}`;
    return fetch(profileUrl).then(response => response.json());
}

function fetchMarketStatus(exchange) {
    var apiKey = 'cnrp4l9r01qqp8d0ir10cnrp4l9r01qqp8d0ir1g'; 
    var profileUrl = `https://finnhub.io/api/v1/stock/market-status?exchange=${exchange}&token=${apiKey}`;
    return fetch(profileUrl).then(response => response.json());
}

function displayQuoteData(quoteData, profileData, ticker, marketStatus) {
	
	var date = new Date();
	var now = (date.getMonth() + 1) + '-' + date.getDate() + '-' +  date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
	
    const stockQuoteDetails = document.getElementById('stockQuoteDetails');
    const summary = document.getElementById('summary');
    currentTicker = ticker;
    stockQuoteDetails.innerHTML = ''; 
	
    let tickerInfo = '';
    let companyName = '';
    let exchangeInfo = '';
    let currentPrice = '';
    let change = '';
    let today = '';
    
    var block = ``;

    let loggedInFeatures = '';
    let user_id = getCookie("user_id");
    if (user_id > 0) {
        
        const priceChange = quoteData.d;
        const percentageChange = quoteData.dp;
		const changeClass = priceChange >= 0 ? 'positive-change' : 'negative-change';
		const changeArrow = priceChange >= 0 ? "<span class='icon-up'></span>" : "<span class='icon-down'></span>";
        
        block = `<div class="left-container">
  					<div id="tickerInfo" class="ticker-info" style="text-align: left">${ticker}</div>
  					<div id="companyName" class="company-name" style="text-align: left">${profileData.name}</div>
  					<div id="exchangeInfo" class="exchange" style="text-align: left">${profileData.exchange}</div>
    				<div id="userFeatures" class="user-features">
    					<div class="quantity">Quantity: <input type="number" name="quantity" id="quantityField" style="width: 25px"></div>
                		<div><button id="buyButton" style="background-color: green">Buy</button></div>
    				</div>
    			</div>
    			<div class="right-container">
    				<div class="current-price ${changeClass}">${quoteData.c.toFixed(2)}</div>
	   			 	<div class="change ${changeClass}">${changeArrow} ${priceChange.toFixed(2)} (${percentageChange}%)</div>
	   			 	<div class="current-day ${changeClass}">${now}</div>
    			</div>`;
        
    } else {
    	
    	block = `<div class="center-container">
  					<div id="tickerInfo" class="ticker-info" style="text-align: center">${ticker}</div>
  					<div id="companyName" class="company-name" style="text-align: center">${profileData.name}</div>
  					<div id="exchangeInfo" class="exchange" style="text-align: center">${profileData.exchange}</div>
    			</div>`;
	}
	
	if(marketStatus.isOpen) {
		var status = "open";
	} else {
		var status = "closed";
	}
	

    let summaryBlock = `
    		<h3>Market is ${status}<h3>
            <h2>Summary</h2>
            <p>High Price: ${quoteData.h}</p>
            <p>Low Price: ${quoteData.l}</p>
            <p>Open Price: ${quoteData.o}</p>
            <p>Close Price: ${quoteData.pc}</p>
    `;

    stockQuoteDetails.innerHTML = block; 
	summary.setAttribute("class", "summary");
	summary.innerHTML = summaryBlock;
	

	if(user_id > 0) {
		document.getElementById('buyButton').addEventListener('click', function() {
	        var quantity = document.getElementById('quantityField').value;
	        if(!quantity) {
				alert("FAILED: Purchase not possible.");
			}
	        fetchCurrentPrice(currentTicker, quantity);
	    });
	}

}

function displayProfileData(data) {
    const stockProfileDetails = document.getElementById('stockProfileDetails');

    stockProfileDetails.innerHTML = '';

    let companyInfo = `
        <div class="company-info">
		    <h2>Company Information</h2>
		    <div><span class="info-label">IPO Date:</span> ${data.ipo}</div>
		    <div><span class="info-label">Market Cap (SM):</span> ${data.marketCapitalization}</div>
		    <div><span class="info-label">Share Outstanding:</span> ${data.shareOutstanding}</div>
		    <div><span class="info-label">Website:</span> <a href="${data.weburl}" target="_blank">${data.weburl}</a></div>
		    <div><span class="info-label">Phone:</span> ${data.phone}</div>
		</div>
    `;

    stockProfileDetails.innerHTML = companyInfo;
}

function fetchCurrentPrice(ticker, quantity) {
    const apiKey = 'cnrp4l9r01qqp8d0ir10cnrp4l9r01qqp8d0ir1g';
    const quoteUrl = `https://finnhub.io/api/v1/quote?symbol=${ticker}&token=${apiKey}`;

    fetch(quoteUrl)
        .then(response => response.json())
        .then(data => {
            if (data && data.c) { 
                buyStock(ticker, quantity, data.c); 
            } else {
                alert('Failed to fetch current stock price.');
            }
        })
        .catch(error => {
            console.error('Error fetching stock price:', error);
            alert('Failed to verify ticker symbol due to a network error.');
        });
}

function buyStock(ticker, numStock, price) {
    const user_id = getCookie("user_id");
    var tradeAction = "BUY"
    fetch('TradeServlet', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ ticker, numStock, user_id, price, tradeAction })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const totalCost = numStock * price;
            alert(`Bought ${numStock} shares of ${ticker} for $${totalCost.toFixed(2)}`);
        } else {
            alert(data.message);
        }
    })
    .catch(error => console.error('Error purchasing stock:', error));
}

function showOnlyMainContent() {
    document.querySelector('.search-container').style.display = 'none';
    document.querySelector('.header').style.display = 'none';
}