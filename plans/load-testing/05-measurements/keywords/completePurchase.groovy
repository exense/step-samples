// One custom measurement wrapping three inner ones, so the report shows both
// the end-to-end journey and where inside it the time went.
//
// Measurements nest: stopMeasure() closes the most recently opened one.
output.startMeasure("Purchase journey")
output.startMeasure("Search")
Thread.sleep(80)
output.stopMeasure()
output.startMeasure("Add to cart")
Thread.sleep(60)
output.stopMeasure()
output.startMeasure("Pay")
Thread.sleep(140)
output.stopMeasure(["paymentMethod": "card"])
output.stopMeasure()
output.add("status", "CONFIRMED")
