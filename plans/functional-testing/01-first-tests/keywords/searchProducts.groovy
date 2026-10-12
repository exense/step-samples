// Simulates a catalogue search: three products whose name contains the search term.
def query = input.getString("query", "")
output.add("resultCount", 3)
output.add("firstProduct", "Wireless " + query)
