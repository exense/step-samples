using System;
using System.IO;

namespace Step.Examples.E2ETesting.Playwright
{
    /// <summary>
    /// Prepares Playwright on a Step agent. Two things are not covered by the package itself:
    ///  - Playwright drives browsers through a bundled Node executable. A package zipped on
    ///    Windows carries no Unix permission bits, so on a Linux agent that executable has to
    ///    be made executable again, otherwise starting it fails with "Permission denied".
    ///  - The browsers are not part of the package and are downloaded on first use.
    /// </summary>
    internal static class PlaywrightBrowsers
    {
        private static readonly object Lock = new object();
        private static bool ready;

        internal static void EnsureReady()
        {
            lock (Lock)
            {
                if (ready)
                {
                    return;
                }

                MakeNodeDriverExecutable();

                int exitCode = Microsoft.Playwright.Program.Main(new[] { "install", "chromium" });
                if (exitCode != 0)
                {
                    throw new Exception("Playwright browser installation failed with exit code " + exitCode);
                }

                ready = true;
            }
        }

        private static void MakeNodeDriverExecutable()
        {
            if (OperatingSystem.IsWindows())
            {
                return;
            }

            const UnixFileMode executable = UnixFileMode.UserRead | UnixFileMode.UserWrite | UnixFileMode.UserExecute;

            foreach (string node in Directory.GetFiles(Directory.GetCurrentDirectory(), "node", SearchOption.AllDirectories))
            {
                File.SetUnixFileMode(node, executable);
            }
        }
    }
}