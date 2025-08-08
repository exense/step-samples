using Microsoft.VisualStudio.TestTools.UnitTesting;
using Step.Functions.IO;
using Step.Handlers.NetHandler;
using System;
using System.Collections.Generic;

namespace SeleniumTest
{
    [TestClass]
    public class KeywordsTests : IDisposable
    {
        ExecutionContext Runner;
        Output Output;
        public KeywordsTests()
        {
            Runner = KeywordRunner.GetExecutionContext(typeof(Keywords));
        }

        [TestMethod]
        public void OpenChromeTest()
        {
            Output = Runner.Run("Open_Chrome", @"{}", new Dictionary<string, string>() { { "headless", @"false" } });
            if (Output.error != null)
            {
                Console.WriteLine(Output.error.msg);
            }
            CheckNoError(Output);

            Output = Runner.Run("Search_in_google", @"{search:'exense'}");

            CheckNoError(Output);

            Assert.AreEqual("https://www.exense.ch/", (string)Output.payload["exense: Home"]);
        }

        [TestMethod]
        public void OpenSafariTest()
        {
            Output = Runner.Run("Open_Safari", @"{}", new Dictionary<string, string>() { { "headless", @"false" } });
            CheckNoError(Output);

            Output = Runner.Run("Search_in_google", @"{search:'exense'}");
            CheckNoError(Output);
            Assert.AreEqual("https://www.exense.ch/", (string)Output.payload["exense: Home"]);
        }

        private void CheckNoError(Output ouput)
        {
            if (ouput.error != null)
            {
                Console.WriteLine(ouput.error.msg);
            }
            Assert.IsNull(ouput.error);
        }

        public void Dispose()
        {
            Runner.Close();
        }
    }
}
