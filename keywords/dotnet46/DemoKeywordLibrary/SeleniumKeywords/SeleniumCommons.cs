using OpenQA.Selenium;
using Step.Functions.IO;

namespace SeleniumTest
{
    public class Wrapper : ICloseable
    {
        public IWebDriver driver;

        public Wrapper(IWebDriver driver)
        {
            this.driver = driver;
        }

        public void Close()
        {
            if (driver != null)
            {
                driver.Quit();
            }
        }
    }
}
