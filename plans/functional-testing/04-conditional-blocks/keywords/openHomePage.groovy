// Simulates opening the home page. First-time visitors get a cookie banner.
output.add("cookieBannerShown", input.getBoolean("firstVisit", false))
