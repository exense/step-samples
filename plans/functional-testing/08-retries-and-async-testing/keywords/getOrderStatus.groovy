// Simulates asynchronous fulfilment: an order is reported as shipped from the third status request on.
def orderId = input.getString("orderId", "")
def polls = (session.get("polls-" + orderId) ?: 0) + 1
session.put("polls-" + orderId, polls)
output.add("orderId", orderId)
output.add("status", polls >= 3 ? "SHIPPED" : "PROCESSING")
