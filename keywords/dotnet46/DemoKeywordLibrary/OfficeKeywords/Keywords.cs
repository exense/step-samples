using Outlook = Microsoft.Office.Interop.Outlook;
using System.Diagnostics;
using System.Linq;
using System;
using Step.Handlers.NetHandler;
using Step.Functions.IO;
using log4net;
using Xunit;

namespace OfficeTest
{
    public class Keywords : AbstractKeyword
    {
        /**
         * see the file AssemblyInfo.cs for needed configuration
         */
        protected static readonly ILog logger = LogManager.GetLogger(typeof(Keywords));

        private Outlook.Application GetApplication()
        {
            Type outlookType = Type.GetTypeFromProgID("Outlook.Application");

            if (outlookType == null)
            {
                return null;
            }
            return (Outlook.Application)Activator.CreateInstance(outlookType);
        }

        [Keyword]
        public void StartOutlook()
        {
            logger.Info("Trying to start Outlook");
            if (Process.GetProcessesByName("OUTLOOK").Count() == 0)
            {
                Outlook.Application outlook;
                if ((outlook = GetApplication()) == null)
                {
                    logger.Error("Outlook is not installed on this agent");
                    output.SetBusinessError("Outlook seems to not be installed on this machine. Aborting");
                    return;
                }

                Outlook.MAPIFolder inbox =
                    outlook.GetNamespace("MAPI").GetDefaultFolder(Outlook.OlDefaultFolders.olFolderInbox);

                inbox.Display();
            }
        }

        [Keyword]
        public void CloseOutlook()
        {
            if (Process.GetProcessesByName("OUTLOOK").Count() > 0)
            {
                Outlook.Application outlook;
                if ((outlook = GetApplication()) == null)
                {
                    output.SetBusinessError("Outlook seems to not be installed on this machine. Aborting");
                    return;
                }

                outlook.Quit();
            }
        }

        [Keyword]
        public void ReadEmails()
        {
            string search = input["search"].ToString();

            Outlook.Application outlook;
            if ((outlook = GetApplication()) == null)
            {
                output.SetBusinessError("Outlook seems to not be installed on this machine. Aborting");
                return;
            }

            Outlook.MAPIFolder inbox =
                outlook.Session.GetDefaultFolder(Outlook.OlDefaultFolders.olFolderInbox);

            foreach (Outlook.MailItem item in inbox.Items.Restrict("[Unread]=true").OfType<Outlook.MailItem>().
                Where(m => m.Subject.Contains(search)).OrderByDescending(m => m.CreationTime))
            {
                item.Display();

                item.UnRead = false;

                item.FlagIcon = Outlook.OlFlagIcon.olBlueFlagIcon;
                item.Categories = "Blue Category";

                item.Close(Outlook.OlInspectorClose.olSave);
            }
        }

        bool received = false;
        private void MailReceived()
        {
            received = true;
        }

        [Keyword]
        public void SendEmail()
        {
            Outlook.Application outlook;
            if ((outlook = GetApplication()) == null)
            {
                output.SetBusinessError("Outlook seems to not be installed on this machine. Aborting");
                return;
            }

            outlook.NewMail += new Outlook.ApplicationEvents_11_NewMailEventHandler(MailReceived);
            received = false;
            Outlook.MailItem mail = (Outlook.MailItem) outlook.CreateItem(Outlook.OlItemType.olMailItem);

            mail.Display();

            mail.To = outlook.Session.CurrentUser.Address;

            mail.Subject = input["subject"].ToString();

            mail.Body = "This is a test";

            mail.Send();

            while (!received) System.Threading.Thread.Sleep(500);
        }
    }

    public class KeywordsTests : IDisposable
    {
        ExecutionContext Runner;
        Output Output;

        public KeywordsTests()
        {
            Runner = KeywordRunner.GetExecutionContext(typeof(Keywords));
        }
        
        [Fact(Skip = "build")]
        public void SendEmailTest()
        {
            Output = Runner.Run("StartOutlook");
            Assert.Null(Output.error);

            Output = Runner.Run("SendEmail", "{subject:'This is a test - email 1'}");
            Assert.Null(Output.error);

            Output = Runner.Run("SendEmail", "{subject:'This is a test - email 2'}");
            Assert.Null(Output.error);

            Output = Runner.Run("SendEmail", "{subject:'This is a test - email 3'}");
            Assert.Null(Output.error);

            Output = Runner.Run("ReadEmails", "{search:'This is a test'}");
            Assert.Null(Output.error);

            Output = Runner.Run("CloseOutlook");
            Assert.Null(Output.error);
        }

        public void Dispose()
        {
            Runner.Close();
        }
    }
}