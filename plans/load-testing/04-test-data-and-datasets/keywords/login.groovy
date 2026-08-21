// Simulates a login and echoes the account back, so the plan can prove which
// row of the data set this virtual user was handed.
Thread.sleep(60)
output.add("account", input.getString("user", "NONE"))
output.add("status", "LOGGED_IN")
