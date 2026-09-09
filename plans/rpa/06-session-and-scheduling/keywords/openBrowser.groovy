// Simulates opening a browser and parking the handle in the AGENT SESSION.
// Anything put in `session` lives on the agent token, not in the plan.
def handle = "BROWSER-" + System.currentTimeMillis()
session.put("browser", handle)
output.add("browser", handle)
