// Simulates placing an order. The order id is derived from the inputs.
def customer = input.getString("customer", "")
def quantity = input.getInt("quantity", 0)
output.add("orderId", "ORD-" + customer.toUpperCase() + "-" + quantity)
output.add("status", "CREATED")
