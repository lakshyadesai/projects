window.addEventListener('load', () => {
    const loginForm = document.getElementById('login');
    const registerForm = document.getElementById('signup');

    loginForm.addEventListener('submit', function(event) {
        if (!validateLoginForm()) {
            event.preventDefault();  // Prevent form from submitting if validation fails
        }
    });

    registerForm.addEventListener('submit', function(event) {
        if (!validateRegisterForm()) {
            event.preventDefault();  // Prevent form from submitting if validation fails
        }
    });

    function validateLoginForm() {
        const username = document.getElementById('login_username').value;
        const password = document.getElementById('login_password').value;

        // Simple validation check
        if (username.length === 0 || password.length === 0) {
            alert("Username and password must not be empty.");
            return false;
        }
        return true;
    }

    function validateRegisterForm() {
        const email = document.getElementById('email').value;
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // Simple validation check
        if (username.length === 0 || password.length === 0 || confirmPassword.length === 0 || email.length === 0) {
            alert("Please fill out all fields.");
            return false;
        }
         
        if (password !== confirmPassword) {
            alert("Passwords do not match.");
            return false;
        }

        return true;
    }
});
