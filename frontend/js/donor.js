document.addEventListener("DOMContentLoaded", () => {
    const signupForm = document.querySelector("#signup-form");
    const verifyButton = document.querySelector("#verify-button");
    const submitOtpButton = document.querySelector("#submit-otp");
    const closePopupButton = document.querySelector("#close-popup");
    const otpPopup = document.querySelector("#otp-popup");
    const signupSubmitButton = document.querySelector("#signup-submit");
    let phoneNumber = "";
    let isVerified = false;

    setTimeout(() => {
        console.log("Checking elements...");
        const elements = [
            "#signup-form", "#verify-button", "#submit-otp",
            "#close-popup", "#otp-popup", "#signup-submit"
        ];

        let missingElements = elements.filter(id => !document.querySelector(id));

        if (missingElements.length > 0) {
            console.error("Missing elements in DOM:", missingElements);
        } else {
            console.log("All elements found.");
        }
    }, 1000); 

    // Disable signup button initially
    signupSubmitButton.disabled = true;

    verifyButton.addEventListener("click", async () => {
        phoneNumber = document.querySelector("#contact")?.value.trim(); 

        if (!phoneNumber) {
            alert("Please enter a valid phone number.");
            return;
        }

        try {
            let response = await fetch("http://localhost:9090/api/otp/send", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ phoneNumber })
            });

            if (response.ok) {
                alert("OTP sent to " + phoneNumber);
                otpPopup.style.display = "block"; 
            } else {
                alert("Failed to send OTP.");
            }
        } catch (error) {
            console.error("Error sending OTP:", error);
            alert("Server error.");
        }
    });

    submitOtpButton.addEventListener("click", async () => {
        let otpCode = document.querySelector("#otp-code")?.value.trim(); // Ensure input exists

        if (!otpCode) {
            alert("Enter the OTP received.");
            return;
        }

        try {
            let response = await fetch("http://localhost:9090/api/otp/verify", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ phoneNumber, otp: otpCode })
            });

            let result = await response.json();
            if (response.ok) {
                alert(result.message);
                isVerified = true;
                signupSubmitButton.disabled = false; // Enable signup button
                otpPopup.style.display = "none"; // Close popup
                verifyButton.textContent = "Verified";
                verifyButton.disabled = true; // Disable button after verification
                verifyButton.style.backgroundColor = "green"; // Change button color
                verifyButton.style.cursor = "not-allowed"; // Change cursor to indicate no more clicks
            } else {
                alert("Invalid OTP. Try again.");
            }
        } catch (error) {
            console.error("Error verifying OTP:", error);
            alert("Server error.");
        }
    });

    closePopupButton.addEventListener("click", () => {
        otpPopup.style.display = "none";
    });

    signupForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        if (!isVerified) {
            alert("Please verify your phone number before signing up.");
            return;
        }

        let nameInput = document.querySelector("#name");
        let emailInput = document.querySelector("#email");
        let passwordInput = document.querySelector("#password");
        let govIdInput = document.querySelector("#govId");

        if (!nameInput || !emailInput || !passwordInput || !govIdInput) {
            console.error("One or more input fields are missing.");
            return;
        }

        let formData = new FormData();
        formData.append("name", nameInput.value.trim());
        formData.append("email", emailInput.value.trim());
        formData.append("password", passwordInput.value);
        formData.append("contactNo", phoneNumber);
        formData.append("govId", govIdInput.files[0]);

        try {
            const response = await fetch("http://localhost:9090/api/donors/register", {
                method: "POST",
                body: formData
            });

            if (response.ok) {
                alert("Registration Successful!");
                signupForm.reset(); // Clear form after successful registration
                signupSubmitButton.disabled = true; // Disable signup again after registration
                isVerified = false;
            } else {
                alert("Registration Failed!");
            }
        } catch (error) {
            console.error("Error during registration:", error);
            alert("Error connecting to server.");
        }
    });
});




document.querySelector("#login-form").addEventListener("submit", async (event) => {
    event.preventDefault();

    const loginData = {
        email: document.querySelector("#loginemail").value,
        password: document.querySelector("#loginpassword").value
    };

    try {
        let response = await fetch("http://localhost:9090/api/donors/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(loginData)
        });

        if (!response.ok) {
            throw new Error("Invalid credentials. Please check your email and password.");
        }

        let data = await response.json();

        // Store the donor ID and token in sessionStorage and localStorage
        sessionStorage.setItem("donorId", data.donorId);  // Store donorId in sessionStorage
        localStorage.setItem("token", data.token);  // Store token in localStorage

        window.location.href = "donorfp.html";  // Redirect to profile page
    } catch (error) {
        alert(error.message);
    }
});


