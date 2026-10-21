// Simulates adding a product to the customer's cart.
output.add("cartId", "CART-" + input.getString("customerId", ""))
output.add("items", 1)
