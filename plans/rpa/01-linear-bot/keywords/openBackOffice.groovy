// Simulates opening the back-office application.
//
// The application context (here a fake handle; in a real bot the Playwright
// or Selenium driver) goes into the agent SESSION - not into the output.
// Every keyword that runs on the same agent token can read it back, so the
// PLAN never has to carry a session id around.
session.put("appContext", "BACKOFFICE-" + System.currentTimeMillis())
output.add("status", "OPEN")
