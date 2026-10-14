// Simulates reading one record from the back-office application.
def recordId = input.getString("recordId", "REC-001")
output.add("recordId", recordId)
output.add("amount", 1250)
output.add("status", "PENDING")
