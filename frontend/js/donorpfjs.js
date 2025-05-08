let donorId = sessionStorage.getItem("donorId"); 

if (!donorId) {
    alert("Session expired. Redirecting to login...");
    window.location.href = "donar.html";  
}

async function fetchProfile(donorId) {
    if (!donorId) {
        alert("Session expired. Please log in again.");
        window.location.href = "donar.html";  
        return;
    }

    try {
        let response = await fetch(`http://localhost:9090/api/donors/profile/${donorId}`, {
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            alert("Failed to load profile.");
            return;
        }

        let donor = await response.json();

        document.getElementById("nameText").innerText = donor.name || "N/A";
        document.getElementById("emailText").innerText = donor.email || "N/A";
        document.getElementById("contactNoText").innerText = donor.contactNo || "N/A";
        document.getElementById("locationText").innerText = donor.location || "N/A";

        if (donor.profilePicture) {
            document.getElementById("profileImage").src = donor.profilePicture;
        } else {
            document.getElementById("profileImage").src = "default-profile.jpg"; 
        }

    } catch (error) {
        console.error("Error fetching profile:", error);
    }
}

document.addEventListener("DOMContentLoaded", function () {
    fetchProfile(donorId);  // Fetch profile details using donorId
});


function openPopup(field) {
    currentField = field;
    document.getElementById("editFieldName").innerText = field.charAt(0).toUpperCase() + field.slice(1);
    document.getElementById("editInput").value = document.getElementById(field + "Text").innerText;
    document.getElementById("editPopup").style.display = "block";
}

function closePopup() {
    document.getElementById("editPopup").style.display = "none";
}

async function saveProfile() {
    let newValue = document.getElementById("editInput").value;
    let updatedData = {};
    updatedData[currentField] = newValue;

    try {
        let response = await fetch(`http://localhost:9090/api/donors/update/${donorId}`, {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token"),
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updatedData)
        });

        if (!response.ok) {
            alert("Failed to update profile.");
            return;
        }

        let updatedDonor = await response.json();
        document.getElementById(currentField + "Text").innerText = updatedDonor[currentField];

        closePopup();
    } catch (error) {
        console.error("Error updating profile:", error);
    }
}

async function uploadProfilePhoto(event) {
    let file = event.target.files[0];
    if (!file) return;

    if (!donorId) {
        alert("Donor ID not found. Please log in again.");
        return;
    }

    let formData = new FormData();
    formData.append("file", file);

    try {
        let response = await fetch(`http://localhost:9090/api/photos/upload/${donorId}`, {
            method: "POST",
            body: formData
        });

        if (response.ok) {
            let data = await response.json();
            let imageUrl = data.photoUrl; // Ensure this matches the backend key "photoUrl"

            if (imageUrl) {
                document.getElementById("profileImage").src = imageUrl; 
                sessionStorage.setItem(`profileImage_${donorId}`, imageUrl);  
                alert("Profile image updated successfully");
            } else {
                alert("Failed to get the image URL");
            }
        } else {
            let errorData = await response.json();
            console.error("Upload failed:", errorData);
            alert("Failed to upload image: " + (errorData.error || "Unknown error"));
        }
    } catch (error) {
        console.error("Error uploading profile photo:", error);
        alert("Error uploading image");
    }
}

// Check donor ID when page is loaded
document.addEventListener("DOMContentLoaded", function () {
    if (!donorId) {
        alert("Session expired. Please log in again.");
        window.location.href = "donar.html";  // Redirect to login if donorId is missing
    } else {
        console.log("Donor ID found:", donorId);
        fetchProfile(donorId);
    }
});

