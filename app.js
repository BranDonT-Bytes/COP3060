console.log("Welcome to Brandon Brown's Interactive Page!");
console.log("Repo URL: https://github.com/yourusername/your-repo");

const name = "Brandon";
const age = 22;
const isStudent = true;
const favoriteCourses = ["Data Structures", "Operating Systems", "AI", "Web Dev", "Cybersecurity"];
const userProfile = { major: "Computer Science", year: "Senior", university: "FAMU" };
let optionalField = undefined;

const sum = age + 5;
const isNameMatch = name === "Brandon";
const eligibleToGraduate = isStudent && age > 20;

console.log({ sum, isNameMatch, eligibleToGraduate });

const form = document.querySelector("form");
const status = document.getElementById("status");
const results = document.getElementById("results");

form.addEventListener("submit", handleSubmit);
document.getElementById("first-name").addEventListener("input", () => {
  console.log("First name is being typed...");
});

function handleSubmit(e) {
  e.preventDefault();
  const email = document.getElementById("email").value;
  if (validateEmail(email)) {
    updateStatus("✅ Email is valid!", "green");
    fetchData();
  } else {
    updateStatus("❌ Please enter a valid email address.", "red");
  }
}

function validateEmail(email) {
  const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return regex.test(email);
}

function updateStatus(message, color = "black") {
  status.textContent = message;
  status.style.color = color;
}

async function fetchData() {
  updateStatus("Loading...", "blue");
  try {
    const res = await fetch("https://jsonplaceholder.typicode.com/users");
    if (!res.ok) throw new Error("Network error");
    const data = await res.json();
    renderUsers(data);
    addSortControl(data);
  } catch (err) {
    updateStatus("Error loading data. Please try again.", "red");
    console.error(err);
  }
}
