// Simulates submitting the requested record through the back-office UI.
// The application context comes from the session, not from a plan variable.
def app = session.get("appContext")
if (app == null) {
    output.setBusinessError("No application context - is this keyword inside the session block?")
    return
}
def recordId = input.getString("recordId", "REC-001")
output.add("confirmationId", "CONF-" + recordId)
output.add("receivedRecordId", recordId)
output.add("receivedAmount", input.getString("amount", "<MISSING>"))
output.add("status", "SUBMITTED")
