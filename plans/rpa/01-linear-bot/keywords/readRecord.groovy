// Simulates reading one record from the back-office application.
// The application context comes from the session, not from a plan variable.
def app = session.get("appContext")
if (app == null) {
    output.setBusinessError("No application context - is this keyword inside the session block?")
    return
}
def recordId = input.getString("recordId", "REC-001")
output.add("recordId", recordId)
output.add("customer", "ACME Corp")
output.add("recordType", "INVOICE")
output.add("amount", 1250)
output.add("status", "PENDING")
