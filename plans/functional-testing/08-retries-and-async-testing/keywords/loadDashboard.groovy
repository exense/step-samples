// Simulates a dashboard that fails to load on the first two attempts of a session.
// The failures are artificial, to give retryIfFails something to absorb.
def attempt = (session.get("dashboardAttempts") ?: 0) + 1
session.put("dashboardAttempts", attempt)
if (attempt <= 2) { output.setBusinessError("Dashboard did not load (attempt " + attempt + ")"); return }
output.add("attempts", attempt)
