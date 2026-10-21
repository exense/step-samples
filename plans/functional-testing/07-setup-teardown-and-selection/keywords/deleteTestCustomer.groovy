// Simulates deleting the test customer. Fails if no customer id is given.
def customerId = input.getString("customerId", "")
if (!customerId.startsWith("CUST-")) { output.setBusinessError("No test customer to delete"); return }
output.add("deleted", customerId)
