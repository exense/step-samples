// Simulates a login. Only the demo credentials are accepted.
def ok = input.getString("username", "") == "shop-tester" && input.getString("password", "") == "demo-secret"
output.add("status", ok ? "LOGGED_IN" : "REJECTED")
