// Simulates a login. Known users with the demo password are accepted.
def ok = input.getString("username", "") in ["jane", "john"] && input.getString("password", "") == "demo-secret"
output.add("status", ok ? "LOGGED_IN" : "REJECTED")
