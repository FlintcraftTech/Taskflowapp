# TOOLS.md

Environment facts learned in sessions, one line per fact. Written in the moment,
so a later session does not rediscover them.

- JDK: `C:\Program Files\Android\Android Studio\jbr` — Android Studio's bundled JBR. There is no `java` on PATH and `JAVA_HOME` is unset, so any Gradle command must set `JAVA_HOME` to that path first. (2026-08-31)
- Android SDK: `local.properties` points at `C:\Users\Alex\AppData\Local\Android\Sdk` — note `Alex`, not the current profile `Alex 2`. That path does exist, so the pointer is correct despite looking stale. (2026-08-31)
- Gradle fails from Claude's shell: every invocation dies with `java.io.IOException: Unable to establish loopback connection`, caused by `java.net.SocketException: Invalid argument: connect` inside `SocketConnection$SocketInputStream` — Gradle's NIO selector cannot open its internal loopback socket pair. Not fixed by `--no-daemon`, `--no-watch-fs`, matching `org.gradle.jvmargs`, `-Djava.net.preferIPv4Stack=true`, or running unsandboxed. Plain TCP loopback (IPv4 and IPv6) works fine from PowerShell, so it is specific to the JVM process Gradle forks. (2026-08-31)
- Android Studio builds this project successfully on the same machine — confirmed by the user 2026-08-31. So the environment is present and only Claude's shell is blocked. (2026-08-31)
- The "Unable to delete directory …\app\build" failure hits Android Studio's builds too, not just the command line — every Gradle task fails at once with the same message, which reads like dozens of errors but is one file lock. Deleting `app\build` from Claude's shell clears it and the next build succeeds; the folder is git-ignored generated output (~59 MB), so deleting it is safe and loses nothing. (2026-09-01)
- Likely cause of that lock: the project lives inside Google Drive (`My Drive\…`) and DriveFS syncs `app\build` as it is written. Moving the project outside Drive would probably end the problem for good — not tried, so this is a hypothesis, not a finding. (2026-09-01)
- Consequence: Claude cannot compile, test or install this app. Compiling is Android Studio's job, run by the user. Code items therefore tick UNCONFIRMED, naming the build as the check nobody has run. (2026-08-31)
