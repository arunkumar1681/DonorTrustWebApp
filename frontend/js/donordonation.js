
document.querySelector(".post-btn").addEventListener("click", async (event) => {
    event.preventDefault();

    let donorId = sessionStorage.getItem("donorId");
    let token = localStorage.getItem("token");

    if (!donorId || !token) {
        alert("You must be logged in to donate.");
        return;
    }

    let formData = new FormData();
    formData.append("donationType", document.getElementById("donationType").value);
    formData.append("description", document.getElementById("donationDescription").value.trim());
    formData.append("quantity", document.getElementById("donationQuantity").value.trim());
    formData.append("location", document.getElementById("donationLocation").value.trim());
    formData.append("photo", document.getElementById("donationPhoto").files[0]); // File input
    formData.append("donorId", donorId);

    try {
        const response = await fetch("http://localhost:9090/api/donations/add", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token 
            },
            body: formData
        });

        const result = await response.json();
        if (response.ok) {
            alert("Donation successfully posted!");
        } else {
            alert(result.error || "Donation failed!");
        }
    } catch (error) {
        console.error("Error:", error);
        alert("Error connecting to server.");
    }
});











