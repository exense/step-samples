// Simulates a product search against the shop API.
Thread.sleep(80)
output.add("firstProductId", "P-" + input.getString("term", "laptop").toUpperCase())
output.add("resultCount", 12)
