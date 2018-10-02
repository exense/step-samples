var DriverWrapper = Java.type("step.script.selenium.DriverWrapper")
var ChromeDriver = Java.type("org.openqa.selenium.chrome.ChromeDriver")
driverWrapper = new DriverWrapper(new ChromeDriver())
session.put("driver",driverWrapper)