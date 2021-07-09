using System;
using System.Collections.Generic;
using log4net;
using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;
using Step.Grid.IO;
using Step.Handlers.NetHandler;

namespace SeleniumTest
{
    public class Keywords : AbstractKeyword
    {
        /**
         * see the file AssemblyInfo.cs for needed configuration
         */
        protected static readonly ILog logger = LogManager.GetLogger(typeof(Keywords));

        [Keyword(name = "Open_Chrome_and_search_in_Google")]
        public void OpenChromeAndSearchInGoogle()
        {
            ChromeDriver driver = CreateDriver();
            GoogleSearch(driver);
            driver.Quit();
        }


        [Keyword(name = "Open_Chrome")]
        public void OpenChrome()
        {
            ChromeDriver driver = CreateDriver();

            session.Put("driver", new Wrapper(driver));
        }

        private ChromeDriver CreateDriver()
        {
            logger.Info("Creating a Chrome Driver");

            ChromeOptions options = new ChromeOptions();
            if (properties.ContainsKey("headless") && Boolean.Parse(properties["headless"]))
            {
                options.AddArguments(new string[] { "headless", "disable-infobars", "disable-gpu", "no-sandbox" });
            }

            ChromeDriver driver = new ChromeDriver(options);

            driver.Manage().Timeouts().ImplicitWait = TimeSpan.FromSeconds(10);

            return driver;
        }

        [Keyword(name = "Navigate_to_URL", properties = new string[] { "url_exense" })]
        public void Navigate()
        {
            IWebDriver driver = GetDriver();
            output.StartMeasure("Navigate");
            driver.Url = (string)properties["url_exense"];
            output.StopMeasure();
        }

        [Keyword(name = "Search_in_google")]
        public void SearchInGoogle()
        {
            IWebDriver driver = GetDriver();

            GoogleSearch(driver);
        }

        private void GoogleSearch(IWebDriver driver)
        {
            if (input["search"] != null)
            {
                if (driver == null)
                {
                    throw new Exception("Please first execute keyword \"Open Chome\" in order to have a driver available for this keyword");
                }

                driver.Url = "http://www.google.com";
                driver.Navigate();

                //IWebElement searchInput = driver.FindElement(By.XPath("//input[@name='q']"));

                IWebElement searchInput = driver.FindElement(By.Name("q"));

                String searchString = (string)input["search"];
                searchInput.SendKeys(searchString + Keys.Enter);

                IWebElement resultCountDiv = driver.FindElement(By.XPath("//div/nobr"));

                ICollection<IWebElement> resultHeaders = driver.FindElements(By.XPath("//div[@class='r']//h3"));
                foreach (IWebElement result in resultHeaders)
                {
                    output.Add(result.Text, result.FindElement(By.XPath("..//cite")).Text);
                }
                Screenshot ss = ((ITakesScreenshot)driver).GetScreenshot();

                string screenshot = ss.AsBase64EncodedString;
                byte[] screenshotAsByteArray = ss.AsByteArray;
                output.AddAttachment(AttachmentHelper.GenerateAttachmentFromByteArray(screenshotAsByteArray,"screenshot.png"));
            }
            else
            {
                throw new Exception("Input parameter 'search' not defined");
            }
        }

        private IWebDriver GetDriver()
        {
            Wrapper wrapper = (Wrapper)session.Get("driver");

            IWebDriver driver = wrapper.driver;
            return driver;
        }
    }
}
