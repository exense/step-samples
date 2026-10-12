// Simulates reading an order back. Only ids produced by "Create Order" exist.
def orderId = input.getString("orderId", "")
if (!orderId.startsWith("ORD-")) { output.setBusinessError("Order not found: " + orderId); return }
output.add("orderId", orderId)
output.add("status", "CONFIRMED")
