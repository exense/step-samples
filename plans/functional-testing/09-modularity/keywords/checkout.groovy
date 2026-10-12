// Simulates checking out a cart. The order id is derived from the cart.
def cartId = input.getString("cartId", "")
if (!cartId.startsWith("CART-")) { output.setBusinessError("Empty cart"); return }
output.add("orderId", cartId.replace("CART-", "ORD-"))
