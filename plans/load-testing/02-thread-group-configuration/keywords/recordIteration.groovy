// Echoes the thread-group counters back, so the plan can prove they arrived
// as values rather than as the literal text of the expression.
Thread.sleep(80)
output.add("orderRef", input.getString("orderRef", "NONE"))
output.add("shopper", String.valueOf(input.getInt("shopper", -1)))
output.add("orderOfThisShopper", String.valueOf(input.getInt("orderOfThisShopper", -1)))
