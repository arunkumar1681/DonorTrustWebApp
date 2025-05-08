let trustId = sessionStorage.getItem("id");
let authToken = localStorage.getItem("authToken");

if (!trustId || !authToken) {
    alert("Session expired. Redirecting to login...");
    window.location.href = "user.html"; // Redirect if not logged in
}

async function fetchProfile() {
    try {
        let response = await fetch(`http://localhost:9090/api/trusts/${trustId}`, {
            headers: {
                "Authorization": "Bearer " + authToken,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) throw new Error("Failed to load profile.");

        let trust = await response.json();

        document.getElementById("nameText").innerText = trust.trustName || "N/A";
        document.getElementById("emailText").innerText = trust.email || "N/A";
        document.getElementById("contactNoText").innerText = trust.contactNo || "N/A";
        document.getElementById("locationText").innerText = trust.location || "N/A";

        let profileImage = document.getElementById("profileImage");
        let savedImageUrl = sessionStorage.getItem(`profileImage_${trustId}`);

        if (savedImageUrl) {
            profileImage.src = savedImageUrl;
        } else if (trust.profilePictureUrl) {
            let imageUrl = `http://localhost:9090/api/trust/photos/uploads/${trust.profilePictureUrl}`;
            profileImage.src = imageUrl;
            sessionStorage.setItem(`profileImage_${trustId}`, imageUrl);
        } else {
            profileImage.src = "default-profile.jpg"; 
        }

    } catch (error) {
        console.error("Error fetching profile:", error);
        alert("Error loading profile. Please try again.");
    }
}

document.addEventListener("DOMContentLoaded", fetchProfile);

let currentField = "";
function openPopup(field) {
    currentField = field;
    let fieldElement = document.getElementById(field === "trustName" ? "nameText" : field + "Text");


    if (!fieldElement) {
        console.error(`Element with ID '${field}' not found. Check your HTML!`);
        alert("Error: Unable to edit this field.");
        return;
    }

    document.getElementById("editFieldName").innerText = 
        field === "trustName" ? "Trust Name" : 
        field === "email" ? "Email" :
        field === "contactNo" ? "Contact No" :
        field === "location" ? "Location" : field;

    document.getElementById("editInput").value = fieldElement.innerText;
    document.getElementById("editPopup").style.display = "block";
}

function closePopup() {
    document.getElementById("editPopup").style.display = "none";
}

async function saveProfile() {
    let newValue = document.getElementById("editInput").value.trim();
    if (!newValue) {
        alert("Value cannot be empty!");
        return;
    }

    try {
        let response = await fetch(`http://localhost:9090/api/trusts/${trustId}`, {
            headers: {
                "Authorization": "Bearer " + authToken,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) throw new Error("Failed to fetch current profile data.");

        let trust = await response.json();

        trust[currentField] = newValue;

        let updateResponse = await fetch(`http://localhost:9090/api/trusts/${trustId}`, {
            method: "PUT",
            headers: {
                "Authorization": "Bearer " + authToken,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(trust) 
        });

        if (!updateResponse.ok) throw new Error("Failed to update profile.");

        let updatedTrust = await updateResponse.json();
        let updateFieldId = currentField === "trustName" ? "nameText" : currentField + "Text";
        document.getElementById(updateFieldId).innerText = updatedTrust[currentField];


        closePopup();
        alert("Profile updated successfully!");
    } catch (error) {
        console.error("Error updating profile:", error);
        alert("Error updating profile. Please try again.");
    }
}

async function uploadProfilePhoto(event) {
    let file = event.target.files[0];
    if (!file) return;

    let formData = new FormData();
    formData.append("file", file);

    try {
        let response = await fetch(`http://localhost:9090/api/trust/photos/upload/${trustId}`, {
            method: "POST",
            headers: { "Authorization": "Bearer " + authToken },
            body: formData
        });

        if (!response.ok) throw new Error("Failed to upload profile picture.");

        let data = await response.json();
        let imageUrl = `http://localhost:9090/api/trust/photos/uploads/${data.photoUrl}`;

        if (imageUrl) {
            document.getElementById("profileImage").src = imageUrl;
            sessionStorage.setItem(`profileImage_${trustId}`, imageUrl);
            alert("Profile image updated successfully!");
        } else {
            alert("Failed to retrieve image URL from server.");
        }
    } catch (error) {
        console.error("Error uploading profile photo:", error);
        alert("Error uploading image. Please try again.");
    }
}








