// Simulates the checkout call - the transaction the SLA is written against.
Thread.sleep(120)
output.add("orderId", "ORD-" + input.getString("cartId", "CART-UNKNOWN"))
output.add("status", "CONFIRMED")
