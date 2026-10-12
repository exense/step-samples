// Simulates a login with a customer account.
def customerId = input.getString("customerId", "")
output.add("status", customerId.startsWith("CUST-") ? "LOGGED_IN" : "REJECTED")
