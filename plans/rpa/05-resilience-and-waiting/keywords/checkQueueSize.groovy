// Simulates asking the back office how many items are still queued: the queue
// drains as polling goes on (2 -> 1 -> 0).
//
// The poll number comes IN FROM THE PLAN as a number, so it is read with
// getInt. This keyword is stateless, so the polling loop needs no session.
def polls = input.getInt("polls", 1)
output.add("queueSize", Math.max(0, 3 - polls))
