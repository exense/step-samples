// Simulates checking out a cart.
def cartId = input.getString("cartId", "")
output.add("status", cartId.startsWith("CART-") ? "PLACED" : "EMPTY_CART")
