/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github, Archimedes Trajano (trajano@github), Christian Bayer (chrbayer84@googlemail.com)
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
package de.flapdoodle.embed.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.spy.memcached.MemcachedClient;
import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.memcached.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.memcached.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.store.IArtifactStore;

// CHECKSTYLE:OFF
public class MemcachedRuntimeTest extends TestCase {

	public void testNothing() {

	}

	public void testDistributions() throws IOException, InterruptedException {
		RuntimeConfigBuilder defaultBuilder = new RuntimeConfigBuilder()
				.defaults(Command.MemcacheD);

		IRuntimeConfig config = defaultBuilder.build();

		for (Platform platform : Platform.values()) {
			if (platform == Platform.Solaris
					| platform == Platform.FreeBSD) {
				continue;
			}
			for (IVersion version : Versions
					.testableVersions(Version.Main.class)) {
				int numberChecked = 0;
				for (BitSize bitsize : BitSize.values()) {
					// there is no osx 32bit version for v2.2.1
					boolean skip = (platform == Platform.Windows && bitsize == BitSize.B64)
							|| (platform != Platform.Windows && bitsize == BitSize.B32);
					if (!skip)
						if (!shipThisVersion(platform, version,
								bitsize)) {
							numberChecked++;
							check(config, new Distribution(
									version, platform,
									bitsize));
						}
				}
				assertTrue(numberChecked > 0);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private boolean shipThisVersion(Platform platform, IVersion version,
			BitSize bitsize) {
		// there is no osx 32bit version for v2.2.1 and above
		String currentVersion = version.asInDownloadPath();
		if ((platform == Platform.OS_X) && (bitsize == BitSize.B32)) {
			if (currentVersion.equals(Version.V1_4_22.asInDownloadPath()))
				return true;
			// if
			// (currentVersion.equals(Version.V1_4_14.asInDownloadPath()))
			// return true;
			if (currentVersion.equals(Version.Main.PRODUCTION
					.asInDownloadPath()))
				return true;
			// if (currentVersion.equals(Version.Main.DEPRECATED
			// .asInDownloadPath()))
			// return true;
		}
		return false;
	}

	private void check(IRuntimeConfig runtime, Distribution distribution)
			throws IOException, InterruptedException {
		IArtifactStore astore = runtime.getArtifactStore();
		assertTrue("Check", astore.checkDistribution(distribution));
		IExtractedFileSet memcached = runtime.getArtifactStore()
				.extractFileSet(distribution);
		assertNotNull("Extracted", memcached.executable());
		astore.removeFileSet(distribution, memcached);
		assertFalse("Delete", memcached.executable().exists());
		Thread.sleep(500);
	}

	public void testCheck() throws IOException, InterruptedException {

		Timer timer = new Timer();

		int port = 12345;
		MemcachedProcess memcachedProcess = null;
		MemcachedExecutable memcached = null;

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder().defaults(
				Command.MemcacheD).build();
		MemcachedStarter runtime = MemcachedStarter
				.getInstance(runtimeConfig);

		timer.check("After Runtime");

		try {
			memcached = runtime.prepare(new MemcachedConfig(
					Version.Main.PRODUCTION, port));
			timer.check("After memcached");
			assertNotNull("memcached", memcached);
			memcachedProcess = memcached.start();
			timer.check("After memcachedProcess");

			MemcachedClient jmemcache = new MemcachedClient(
					new InetSocketAddress("localhost", 12345));
			timer.check("After jmemcached");
			// adding a new key
			jmemcache.add("key", 5, "value");
			timer.check("After jmemcache store");
			// getting the key value
			assertEquals("value", jmemcache.get("key"));
			timer.check("After jmemcache get");
		} finally {
			if (memcachedProcess != null)
				memcachedProcess.stop();
			timer.check("After memcachedProcess stop");
			if (memcached != null)
				memcached.stop();
			timer.check("After memcached stop");
		}
		timer.log();
	}

	static class Timer {

		long _start = System.currentTimeMillis();
		long _last = _start;

		List<String> _log = new ArrayList<String>();

		void check(String label) {
			long current = System.currentTimeMillis();
			long diff = current - _last;
			_last = current;

			_log.add(label + ": " + diff + "ms");
		}

		void log() {
			for (String line : _log) {
				System.out.println(line);
			}
		}
	}

}
