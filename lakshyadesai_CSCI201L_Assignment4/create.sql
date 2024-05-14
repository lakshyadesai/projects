CREATE TABLE userinfo (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL
);

CREATE TABLE Portfolio (
    trade_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    ticker VARCHAR(10) NOT NULL,
    numStock INT NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES userinfo(user_id)
);
