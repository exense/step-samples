// Fails on every third iteration, so the failure-rate threshold has something
// to measure. The iteration number comes from the plan, so the keyword stays
// stateless.
//
// "Payment accepted" is emitted only on success, which is what makes the
// success count - and therefore the failure rate - assertable from the plan.
Thread.sleep(80)
def iteration = input.getInt("iteration", -1)
if (iteration < 0) {
    // Distinguishing a mis-wired input from a simulated rejection matters:
    // getInt falls back to its default when the value did not arrive as a
    // NUMBER, and without this the plan would just look uniformly broken.
    output.setBusinessError("No usable iteration number arrived - check that the input coerces the counter.")
} else if (iteration % 3 == 0) {
    output.setBusinessError("Payment provider rejected the transaction.")
} else {
    output.startMeasure("Payment accepted")
    output.stopMeasure()
    output.add("status", "PAID")
}
