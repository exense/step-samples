// Two custom measurements inside a single keyword call, so the report shows
// the two pages separately instead of one opaque "Browse Catalog" total.
output.startMeasure("Catalog page")
Thread.sleep(90)
output.stopMeasure()
output.startMeasure("Product page")
Thread.sleep(140)
output.stopMeasure(["page": "product-detail"])
output.add("status", "OK")
