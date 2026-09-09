// Simulates submitting the record through the back-office UI.
//
// It echoes back the amount it actually received, so the plan can assert the
// value really arrived - a static "${amount}" would show up here as the
// literal string instead of the number.
//
// `amount` arrives as a NUMBER, so it is read with getInt. getString on a
// numeric input throws ClassCastException.
def app = session.get("appContext")
if (app == null) {
    output.setBusinessError("No application context - is this keyword inside the session block?")
    return
}
def recordId = input.getString("recordId", "REC-001")
output.add("confirmationId", "CONF-" + recordId)
output.add("receivedAmount", input.getInt("amount", -1))
output.add("status", "SUBMITTED")
