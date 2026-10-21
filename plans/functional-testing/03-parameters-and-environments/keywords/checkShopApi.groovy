// Simulates a health check of the shop API. The API URL is a technical setting read from the keyword properties.
def apiUrl = properties.get("shopApiUrl")
output.add("apiUrl", apiUrl ?: "MISSING")
output.add("status", apiUrl ? "UP" : "DOWN")
