// Simulates logging into the back-office application.
// The password arrives from a protected Step parameter - never hard-coded.
//
// The resulting application context goes into the agent SESSION, so the
// keywords that follow just find it there. The plan carries no session id.
def user = input.getString("user", "")
def password = input.getString("password", "")
if (password == null || password.isEmpty()) {
    output.setBusinessError("No password supplied - check the 'botPassword' parameter.")
    return
}
session.put("appContext", "BACKOFFICE-" + user)
output.add("loggedInAs", user)
output.add("status", "LOGGED_IN")
