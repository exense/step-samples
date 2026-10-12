// Simulates the pricing service: the discount in percent for a customer.
def discounts = ["acme": 20, "jane": 10, "john": 0]
output.add("discount", discounts[input.getString("customerId", "")] ?: 0)
