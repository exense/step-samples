// Simulates adding a product to the cart. The line total is computed by the shop.
def quantity = input.getInt("quantity", 1)
output.add("quantity", quantity)
output.add("lineTotal", 49 * quantity)
