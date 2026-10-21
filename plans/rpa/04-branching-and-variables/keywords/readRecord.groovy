// Simulates reading one record. The record type and amount drive the routing
// decisions in the plan.
def recordId = input.getString("recordId", "REC-001")
def type = input.getString("recordType", "INVOICE")
def amount = Integer.parseInt(input.getString("amount", "500"))
output.add("recordId", recordId)
output.add("recordType", type)
output.add("amount", amount)
output.add("status", "PENDING")
