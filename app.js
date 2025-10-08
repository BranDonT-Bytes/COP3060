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
