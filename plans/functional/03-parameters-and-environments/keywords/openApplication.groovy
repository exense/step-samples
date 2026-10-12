// Simulates opening the web shop. The URL is a business input; the API URL is read from the keyword properties.
output.add("url", input.getString("url", ""))
output.add("apiUrl", properties.get("shopApiUrl") ?: "MISSING")
output.add("title", "Web shop")
