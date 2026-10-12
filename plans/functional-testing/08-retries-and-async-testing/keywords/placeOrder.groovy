// Simulates placing an order. Fulfilment happens asynchronously in the shop.
output.add("orderId", "ORD-" + UUID.randomUUID().toString().take(8))
output.add("status", "PROCESSING")
