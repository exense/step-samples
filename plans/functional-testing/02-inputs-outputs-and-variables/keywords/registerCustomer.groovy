// Simulates a customer registration. Echoes back what it received, and the JSON type of maxOrders.
output.add("username", input.getString("username", ""))
output.add("email", input.getString("email", ""))
output.add("deliveryDate", input.getString("deliveryDate", ""))
output.add("maxOrdersType", input.get("maxOrders")?.getValueType()?.toString() ?: "MISSING")
