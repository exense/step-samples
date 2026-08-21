// Simulates a UI step that fails on the first two attempts and succeeds on the
// third.
//
// The attempt number comes IN FROM THE PLAN as a number, so it is read with
// getInt - getString on a numeric input throws ClassCastException.
//
// This keyword holds no state of its own, which is what lets each retry run on
// a fresh agent - no session required. A keyword that remembers things between
// calls forces every caller to pin itself to one agent.
def attempt = input.getInt("attempt", 1)
output.add("attempt", attempt)
if (attempt < 3) {
    output.setBusinessError("Simulated transient UI failure on attempt " + attempt)
} else {
    output.add("status", "OK")
}
