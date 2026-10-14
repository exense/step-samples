// Simulates submitting one record through the back-office UI.
def recordId = input.getString("recordId", "UNKNOWN")
output.add("confirmationId", "CONF-" + recordId)
output.add("status", "SUBMITTED")
