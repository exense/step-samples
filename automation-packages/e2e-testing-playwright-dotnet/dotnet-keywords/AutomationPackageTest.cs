using Microsoft.VisualStudio.TestTools.UnitTesting;
using Step.Functions.IO;
using Step.Handlers.NetHandler;
using System;
using System.Collections.Generic;

namespace Step.Examples.E2ETesting.Playwright
{
    /// <summary>
    /// Runs the keywords of this automation package locally, without a Step instance,
    /// using the Step KeywordRunner. This mirrors the test case defined in
    /// automation-package.yaml.
    /// </summary>
    [TestClass]
    public class AutomationPackageTest : IDisposable
    {
        private readonly ExecutionContext runner;

        private readonly Dictionary<string, string> stepParameters = new Dictionary<string, string>
        {
            { "targetUrl", "https://opencart-prf.stepcloud.ch/" }
        };

        public AutomationPackageTest()
        {
            runner = KeywordRunner.GetExecutionContext(
                typeof(OpenCartPlaywrightKeywords),
                typeof(WebmailPlaywrightKeywords));
        }

        [TestMethod]
        public void OpenCartTestCase()
        {
            Output output = runner.Run("Purchase product in OpenCart", @"{""Product"":""MacBook""}", stepParameters);
            CheckNoError(output);

            output = runner.Run("Read order confirmation in Webmail", @"{}", stepParameters);
            CheckNoError(output);
            Assert.IsNotNull(output.payload["EmailTitle"]);
        }

        private void CheckNoError(Output output)
        {
            if (output.error != null)
            {
                Console.WriteLine(output.error.msg);
            }
            Assert.IsNull(output.error);
        }

        public void Dispose()
        {
            runner.Close();
        }
    }
}