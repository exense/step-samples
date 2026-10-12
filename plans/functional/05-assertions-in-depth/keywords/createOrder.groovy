// Simulates placing an order. A validation error is a normal outcome, reported as outputs.
def quantity = input.getInt("quantity", 0)
output.add("status", quantity >= 1 ? "CREATED" : "REJECTED")
if (quantity < 1) output.add("errorMessage", "Quantity must be at least 1")
