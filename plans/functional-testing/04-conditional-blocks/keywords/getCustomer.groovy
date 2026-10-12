// Simulates looking up a customer and their account type.
def types = ["acme": "BUSINESS", "jane": "PREMIUM", "john": "STANDARD"]
output.add("accountType", types[input.getString("customerId", "")] ?: "UNKNOWN")
