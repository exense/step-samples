// Reads the browser handle back out of the agent session.
// This only works if this keyword ran on the SAME agent token as the one that
// opened the browser - which is exactly what the session control guarantees.
def handle = session.get("browser")
if (handle == null) {
    output.setBusinessError("No browser in this session - the keyword landed on a different agent token.")
    return
}
output.add("browser", handle)
output.add("page", input.getString("page", "home"))
