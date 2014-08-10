/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano	(trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embed.memcached.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.TestCase;
import net.spy.memcached.MemcachedClient;
import de.flapdoodle.embed.memcached.Command;
import de.flapdoodle.embed.memcached.MemcachedExecutable;
import de.flapdoodle.embed.memcached.MemcachedProcess;
import de.flapdoodle.embed.memcached.MemcachedStarter;
import de.flapdoodle.embed.memcached.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.memcached.config.DownloadConfigBuilder;
import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.memcached.config.MemcachedConfig.Net;
import de.flapdoodle.embed.memcached.config.MemcachedConfig.Storage;
import de.flapdoodle.embed.memcached.config.MemcachedConfig.Timeout;
import de.flapdoodle.embed.memcached.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.memcached.distribution.Version;
import de.flapdoodle.embed.memcached.tests.MemcachedForTestsFactory;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.io.directories.UserTempDirInPlatformTempDir;
import de.flapdoodle.embed.process.io.file.Files;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor;
import de.flapdoodle.embed.process.runtime.Network;

public class TestExampleReadMeCode extends TestCase {

	@Override
	protected void setUp() {
		for (String lib : new String[] { "libgcc_s_sjlj-1.dll",
				"mingwm10.dll", "pthreadGC2.dll", "libevent-2.0.so.5" }) {
			Files.forceDelete(new File(PropertyOrPlatformTempDir
					.defaultInstance().asFile(), lib));
		}
	}

	// ### Usage
	public void testStandard() throws UnknownHostException, IOException {
		// ->
		int port = 12345;
		MemcachedConfig memcachedConfig = new MemcachedConfig(
				Version.Main.PRODUCTION, port);

		MemcachedStarter runtime = MemcachedStarter.getDefaultInstance();

		MemcachedExecutable memcachedExecutable = null;
		try {
			memcachedExecutable = runtime.prepare(memcachedConfig);
			MemcachedProcess memcached = memcachedExecutable.start();

			MemcachedClient jmemcache = new MemcachedClient(
					new InetSocketAddress("localhost", 12345));
			// adding a new key
			jmemcache.add("key", 5, "value");
			// getting the key value
			assertEquals("value", jmemcache.get("key"));

		} finally {
			if (memcachedExecutable != null)
				memcachedExecutable.stop();
		}
		// <-
	}

	// ### Usage - custom mongod filename
	public void testCustomMemcachedFilename() throws UnknownHostException,
			IOException {
		// ->
		int port = 12345;
		MemcachedConfig memcachedConfig = new MemcachedConfig(
				Version.Main.PRODUCTION, port);

		Command command = Command.MemcacheD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(command)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(command))
								.tempDir(new UserTempDirInPlatformTempDir())
								.executableNaming(
										new UserTempNaming()))
				.build();

		MemcachedStarter runtime = MemcachedStarter
				.getInstance(runtimeConfig);

		MemcachedExecutable memcachedExecutable = null;
		try {
			memcachedExecutable = runtime.prepare(memcachedConfig);
			memcachedExecutable.start();

			MemcachedClient jmemcache = new MemcachedClient(
					new InetSocketAddress("localhost", 12345));
			// adding a new key
			jmemcache.add("key", 5, "value");
			// getting the key value
			assertEquals("value", jmemcache.get("key"));

		} finally {
			if (memcachedExecutable != null)
				memcachedExecutable.stop();
		}
		// <-
	}

	// ### Unit Tests
	public void testUnitTests() {
		// @include AbstractMemcacheTest.java
		Class<?> see = AbstractMemcacheTest.class;
	}

	// #### ... with some more help
	public void testMemcachedForTests() throws IOException {
		// ->
		// ...
		MemcachedForTestsFactory factory = null;
		try {
			factory = MemcachedForTestsFactory
					.with(Version.Main.PRODUCTION);

			MemcachedClient jmemcache = factory.newMemcachedClient();
			// adding a new key
			jmemcache.add("key", 5, "value");
			// getting the key value
			assertEquals("value", jmemcache.get("key"));

		} finally {
			if (factory != null)
				factory.shutdown();
		}
		// ...
		// <-
	}

	// ### Customize Download URL
	public void testCustomizeDownloadURL() {
		// ->
		// ...
		Command command = Command.MemcacheD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(command)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(
												command)
										.downloadPath(
												"http://my.custom.download.domain/")))
				.build();
		// ...
		// <-
	}

	// ### Customize Artifact Storage
	public void testCustomizeArtifactStorage() throws IOException {

		MemcachedConfig memcachedConfig = new MemcachedConfig(
				Version.Main.PRODUCTION, 12345);

		// ->
		// ...
		IDirectory artifactStorePath = new FixedPath(
				System.getProperty("user.home")
						+ "/.embeddedMemcachedbCustomPath");
		ITempNaming executableNaming = new UUIDTempNaming();

		Command command = Command.MemcacheD;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(command)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(command)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(
												command)
										.artifactStorePath(
												artifactStorePath))
								.executableNaming(
										executableNaming))
				.build();

		MemcachedStarter runtime = MemcachedStarter
				.getInstance(runtimeConfig);
		MemcachedExecutable memcachedExe = runtime.prepare(memcachedConfig);
		// ...
		// <-
		MemcachedProcess memcached = memcachedExe.start();

		memcached.stop();
		memcachedExe.stop();
	}

	// ### Usage - custom memcached process output
	// #### ... to console with line prefix
	public void testCustomOutputToConsolePrefix() {
		// ->
		// ...
		ProcessOutput processOutput = new ProcessOutput(
				Processors.namedConsole("[memcached>]"),
				Processors.namedConsole("[REDISD>]"),
				Processors.namedConsole("[console>]"));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MemcacheD)
				.processOutput(processOutput).build();

		MemcachedStarter runtime = MemcachedStarter
				.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// #### ... to file
	public void testCustomOutputToFile() throws FileNotFoundException,
			IOException {
		// ->
		// ...
		IStreamProcessor memcachedOutput = Processors.named(
				"[memcached>]",
				new FileStreamProcessor(File.createTempFile(
						"memcached", "log")));
		IStreamProcessor memcachedError = new FileStreamProcessor(
				File.createTempFile("memcached-error", "log"));
		IStreamProcessor commandsOutput = Processors
				.namedConsole("[console>]");

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MemcacheD)
				.processOutput(
						new ProcessOutput(memcachedOutput,
								memcachedError, commandsOutput))
				.build();

		MemcachedStarter runtime = MemcachedStarter
				.getInstance(runtimeConfig);
		// ...
		// <-
	}

	/*
	 * Ist fürs Readme, deshalb nicht statisch und public
	 */
	// ->

	// ...
	public class FileStreamProcessor implements IStreamProcessor {

		private final FileOutputStream outputStream;

		public FileStreamProcessor(File file) throws FileNotFoundException {
			outputStream = new FileOutputStream(file);
		}

		@Override
		public void process(String block) {
			try {
				outputStream.write(block.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onProcessed() {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// ...
	// <-

	// #### ... to java logging
	public void testCustomOutputToLogging() throws FileNotFoundException,
			IOException {
		// ->
		// ...
		Logger logger = Logger.getLogger(getClass().getName());

		ProcessOutput processOutput = new ProcessOutput(Processors.logTo(
				logger, Level.INFO), Processors.logTo(logger,
				Level.SEVERE), Processors.named("[console>]",
				Processors.logTo(logger, Level.FINE)));

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MemcacheD, logger)
				.processOutput(processOutput)
				.artifactStore(
						new ArtifactStoreBuilder()
								.defaults(Command.MemcacheD)
								.download(new DownloadConfigBuilder()
										.defaultsForCommand(
												Command.MemcacheD)
										.progressListener(
												new LoggingProgressListener(
														logger,
														Level.FINE))))
				.build();

		MemcachedStarter runtime = MemcachedStarter
				.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// #### ... to default java logging (the easy way)
	public void testDefaultOutputToLogging() throws FileNotFoundException,
			IOException {
		// ->
		// ...
		Logger logger = Logger.getLogger(getClass().getName());

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaultsWithLogger(Command.MemcacheD, logger).build();

		MemcachedStarter runtime = MemcachedStarter
				.getInstance(runtimeConfig);
		// ...
		// <-
	}

	// ### Custom Version
	public void testCustomVersion() throws UnknownHostException, IOException {
		// ->
		// ...
		int port = 12345;
		MemcachedConfig memcachedConfig = new MemcachedConfig(
				new GenericVersion("1.4.15_2"), port);

		MemcachedStarter runtime = MemcachedStarter.getDefaultInstance();
		MemcachedProcess memcached = null;

		MemcachedExecutable memcachedExecutable = null;
		try {
			memcachedExecutable = runtime.prepare(memcachedConfig);
			memcached = memcachedExecutable.start();

			// <-
			MemcachedClient jmemcache = new MemcachedClient(
					new InetSocketAddress("localhost", 12345));
			// adding a new key
			jmemcache.add("key", 5, "value");
			// getting the key value
			assertEquals("value", jmemcache.get("key"));
			// ->
			// ...

		} finally {
			if (memcached != null) {
				memcached.stop();
			}
			if (memcachedExecutable != null)
				memcachedExecutable.stop();
		}
		// ...
		// <-

	}

	// ### Main Versions
	public void testMainVersions() throws UnknownHostException, IOException {
		// ->
		IVersion version = Version.V1_4_15;
		// uses latest supported 2.2.x Version
		// version = Version.Main.V1_4_OLD;
		// uses latest supported production version
		version = Version.Main.PRODUCTION;
		// uses latest supported development version
		// version = Version.Main.DEPRECATED;
		// <-
	}

	// ### Use Free Server Port
	/*
	 * // -> Warning: maybe not as stable, as expected. // <-
	 */
	// #### ... by hand
	public void testFreeServerPort() throws UnknownHostException, IOException {
		// ->
		// ...
		int port = Network.getFreeServerPort();
		// ...
		// <-
	}

	// #### ... automagic
	public void testFreeServerPortAuto() throws UnknownHostException,
			IOException {
		// ->
		// ...
		MemcachedConfig memcachedConfig = new MemcachedConfig(
				Version.Main.PRODUCTION);

		MemcachedStarter runtime = MemcachedStarter.getDefaultInstance();

		MemcachedExecutable memcachedExecutable = null;
		MemcachedProcess memcached = null;
		try {
			memcachedExecutable = runtime.prepare(memcachedConfig);
			memcached = memcachedExecutable.start();

			MemcachedClient jmemcache = new MemcachedClient(
					new InetSocketAddress("localhost", memcached
							.getConfig().net().getPort()));
			// adding a new key
			jmemcache.add("key", 5, "value");
			// getting the key value
			assertEquals("value", jmemcache.get("key"));
			// ->
			// ...

		} finally {
			if (memcached != null) {
				memcached.stop();
			}
			if (memcachedExecutable != null)
				memcachedExecutable.stop();
		}
		// ...
		// <-
	}

	// ### ... custom timeouts
	public void testCustomTimeouts() throws UnknownHostException, IOException {
		// ->
		// ...
		MemcachedConfig memcachedConfig = new MemcachedConfig(
				Version.Main.PRODUCTION, new Net(), new Storage(),
				new Timeout(30000));
		// ...
		// <-
	}

	// ### Command Line Post Processing
	public void testCommandLinePostProcessing() {

		// ->
		// ...
		ICommandLinePostProcessor postProcessor = // ...
		// <-
		new ICommandLinePostProcessor() {
			@Override
			public List<String> process(Distribution distribution,
					List<String> args) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		// ->

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MemcacheD)
				.commandLinePostProcessor(postProcessor).build();
		// ...
		// <-
	}

}
