// Simulates a login. A rejected login is a normal outcome, reported as outputs.
def ok = input.getString("password", "") == "demo-secret"
output.add("status", ok ? "LOGGED_IN" : "REJECTED")
if (!ok) output.add("errorMessage", "Invalid username or password")
