document.addEventListener("DOMContentLoaded", function () {
    fetchDonorHistory();
});

function fetchDonorHistory() {
    const donorId = sessionStorage.getItem("donorId"); 
    if (!donorId) {
        alert("Donor ID not found! Please log in.");
        return;
    }

    fetch(`http://localhost:9090/api/donations/history/${donorId}`)
        .then(response => response.json())
        .then(data => displayDonorHistory(data))
        .catch(error => console.error("Error fetching donor history:", error));
}

function displayDonorHistory(donations) {
    const historyTable = document.getElementById("donationHistory");
    historyTable.innerHTML = ""; 

    if (donations.length === 0) {
        historyTable.innerHTML = `<tr><td colspan="6">No donation history available.</td></tr>`;
        return;
    }

    donations.forEach((donation, index) => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${index + 1}</td>
            <td>${donation.donationType}</td>
            <td>${donation.location}</td>
            <td class="${donation.status ? 'accepted' : 'pending'}">
                ${donation.status ? "Accepted" : "Pending"}
            </td>
            <td>${donation.status ? donation.trustName : "N/A"}</td>
            <td>${donation.status ? donation.trustContactNo : "N/A"}</td>
        `;

        historyTable.appendChild(row);
    });
}
