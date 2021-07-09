using Step.Functions.IO;
using Step.Handlers.NetHandler;
using System;
using System.Collections.Generic;
using Xunit;

namespace SeleniumTest
{
    public class KeywordsTests : IDisposable
    {
        ExecutionContext Runner;
        Output Output;
        public KeywordsTests()
        {
            Runner = KeywordRunner.GetExecutionContext(typeof(Keywords));
        }

        [Fact(Skip = "build")]
        public void OpenChromeTest()
        {
            Output = Runner.Run("Open_Chrome", @"{}", new Dictionary<string, string>() { { "headless", @"false" } });
            Assert.Null(Output.error);

            Output = Runner.Run("Search_in_google", @"{search:'exense'}");
            Assert.Null(Output.error);
            Assert.Equal("www.exense.ch", (string)Output.payload["exense: Home"]);
        }

        public void Dispose()
        {
            Runner.Close();
        }
    }
}
