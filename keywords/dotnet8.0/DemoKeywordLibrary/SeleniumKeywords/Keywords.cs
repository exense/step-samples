using System;
using System.Collections.Generic;
using System.IO;
using log4net;
using OpenQA.Selenium;
using OpenQA.Selenium.Chrome;
using OpenQA.Selenium.Safari;
using Step.Functions.IO;
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
            ChromeDriver driver = CreateChromeDriver();
            GoogleSearch(driver);
            driver.Quit();
        }


        [Keyword(name = "Open_Chrome")]
        public void OpenChrome()
        {
            ChromeDriver driver = CreateChromeDriver();

            session.Put("driver", new Wrapper(driver));
        }

        [Keyword(name = "Open_Safari")]
        public void OpenSafari()
        {
            SafariDriver driver = CreateSafariDriver();

            session.Put("driver", new Wrapper(driver));
        }

        private SafariDriver CreateSafariDriver()
        {
            logger.Info("Creating a Safari Driver");

            SafariOptions options = new SafariOptions();
            if (properties.ContainsKey("headless") && Boolean.Parse(properties["headless"]))
            {
                options.AddAdditionalOption("headless", "true");
            } else
            {
                options.AddAdditionalOption("headless", "true");
            }

            // the safari driver should always be at the same location:
            SafariDriverService service = SafariDriverService.CreateDefaultService("/usr/bin/safaridriver");
            
            SafariDriver driver = new SafariDriver(service, options);

            driver.Manage().Timeouts().ImplicitWait = TimeSpan.FromSeconds(10);

            return driver;
        }
        private ChromeDriver CreateChromeDriver()
        {
            logger.Info("Creating a Chrome Driver");

            ChromeOptions options = new ChromeOptions();
            if (properties.ContainsKey("headless") && Boolean.Parse(properties["headless"]))
            {
                options.AddArguments(new string[] { "headless", "disable-infobars", "disable-gpu", "no-sandbox" });
            }

            string driverPath = (string) (input.ContainsKey("driver") ? input["driver"] : "/usr/bin/chromedriver");

            ChromeDriverService service = ChromeDriverService.CreateDefaultService(driverPath);

            ChromeDriver driver = new ChromeDriver(service,options);

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

                // Accept cookies and conditions
                driver.FindElement(By.XPath("//*[@id='L2AGLb']")).Click();

                IWebElement searchInput = driver.FindElement(By.Name("q"));

                String searchString = (string)input["search"];
                searchInput.SendKeys(searchString + Keys.Enter);

                ICollection<IWebElement> resultHeaders = driver.FindElements(By.XPath("//h3"));
                foreach (IWebElement result in resultHeaders)
                {
                    try
                    {
                        output.Add(result.Text, result.FindElement(By.XPath("..")).GetAttribute("href"));
                    } catch (ArgumentException _) { /*already added to the output*/}
                }
                Screenshot ss = ((ITakesScreenshot)driver).GetScreenshot();

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
