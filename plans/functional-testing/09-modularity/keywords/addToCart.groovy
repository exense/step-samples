// Simulates adding a product to the cart of the logged-in customer.
output.add("cartId", "CART-" + input.getString("username", "").toUpperCase())
output.add("sku", input.getString("sku", ""))
