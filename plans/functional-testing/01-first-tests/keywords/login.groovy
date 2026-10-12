// Simulates logging in to the web shop.
def username = input.getString("username", "")
output.add("status", username ? "LOGGED_IN" : "ANONYMOUS")
output.add("displayName", username.capitalize())
