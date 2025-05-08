let allPosts = []; // Store all posts globally

document.addEventListener("DOMContentLoaded", function () {
    fetchDonations();
});

function fetchDonations() {
    fetch("http://localhost:9090/api/donations/get") // Update with API endpoint
        .then(response => response.json())
        .then(data => {
            // Filter out accepted donations so they don't appear in the feed
            allPosts = data.filter(post => !post.status);  
            displayPosts("Food"); // Show 'Food' posts by default
        })
        .catch(error => console.error("Error fetching donation posts:", error));
}

function filterPosts(category) {
    let mappedCategory = category === "Educational Items" 
    ? "Education Material"  
    : category;

    displayPosts(mappedCategory);

    // Update active tab styling
    document.querySelectorAll(".nav-link").forEach(tab => tab.classList.remove("active"));
    document.querySelector(`.nav-link[onclick="filterPosts('${category}')"]`).classList.add("active");
}

function displayPosts(category) {
    const container = document.getElementById("donationPosts");
    container.innerHTML = "";

    const filteredPosts = allPosts.filter(post => post.donationType === category);

    if (filteredPosts.length === 0) {
        container.innerHTML = `<p class="no-data">No ${category} donations available.</p>`;
        return;
    }

    filteredPosts.forEach(post => {
        const postElement = document.createElement("div");
        postElement.classList.add("card", "post-card");
        postElement.setAttribute("id", `post-${post.id}`);

        postElement.innerHTML = `
            <div class="post-body">
                <h5 class="text-primary"><strong>${post.donorName}</strong></h5>
                <p class="card-text">${post.description}</p>
                <p class="card-text">Quantity: ${post.quantity}</p>
            </div>
            <img src="${post.photoUrl}" class="post-img" alt="Donation Image"
                 style="width: 100%; height: 300px; object-fit: cover; border-radius: 10px; ">
            <div class="post-body">
                <p class="card-text"><strong>Location:</strong> ${post.location}</p>
                <button class="btn btn-success accept-btn" onclick="acceptDonation(${post.id})" id="accept-btn-${post.id}">
                    Accept
                </button>
            </div>
        `;

        container.appendChild(postElement);
    });
}

function acceptDonation(donationId) {
    const trustId = sessionStorage.getItem("id"); // Get Trust ID from session storage
    const trustName = "Trust Name Placeholder"; // Replace with actual trust name logic

    if (!trustId) {
        alert("Trust ID not found! Please log in.");
        return;
    }

    fetch(`http://localhost:9090/api/donations/accept/${donationId}/${trustId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            trustId: trustId,
            trustName: trustName
        }),
    })
    .then(response => response.json())
    .then(() => {
        // Remove accepted donation from global posts array
        allPosts = allPosts.filter(post => post.id !== donationId);
        
        // Re-render posts to reflect removal
        displayPosts(document.querySelector(".nav-link.active").innerText);

        alert("Donation accepted successfully!");
    })
    .catch(error => console.error("Error accepting donation:", error));
}








        