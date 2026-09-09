// Simulates picking up one file from a drop folder and processing it.
def file = input.getString("file", "")
output.add("processedFile", file)
output.add("status", "PROCESSED")
