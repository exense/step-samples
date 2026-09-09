// Simulates submitting one record.
def recordId = input.getString("recordId", "REC-001")
output.add("confirmationId", "CONF-" + recordId)
output.add("status", "SUBMITTED")
