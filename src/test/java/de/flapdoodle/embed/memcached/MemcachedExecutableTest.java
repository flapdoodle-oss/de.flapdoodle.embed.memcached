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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import de.flapdoodle.embed.memcached.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.memcached.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.memcached.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;
import de.flapdoodle.embed.process.runtime.Network;
import junit.framework.Assert;
import junit.framework.TestCase;
import net.spy.memcached.MemcachedClient;

/**
 * Integration test for starting and stopping MongodExecutable
 * 
 * @author m.joehren
 */
// CHECKSTYLE:OFF
public class MemcachedExecutableTest extends TestCase {

	private static final Logger _logger = Logger
			.getLogger(MemcachedExecutableTest.class.getName());

	@Test
	public void testStartStopTenTimesWithNewMemcacheExecutable()
			throws IOException, InterruptedException {
		boolean useMemcache = true;
		int loops = 10;

		int port = Network.getFreeServerPort();
		MemcachedConfig memcachedConfig = new MemcachedConfig(
				Version.Main.PRODUCTION, port);

		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MemcacheD).build();

		for (int i = 0; i < loops; i++) {
			_logger.info("Loop: " + i);
			MemcachedExecutable memcachedExe = MemcachedStarter
					.getInstance(runtimeConfig)
					.prepare(memcachedConfig);
			try {
				MemcachedProcess memcached = memcachedExe.start();

				if (useMemcache) {
					MemcachedClient jmemcache = new MemcachedClient(
							new InetSocketAddress("localhost",
									port));
					// adding a new key
					jmemcache.add("key", 5, "value");
					// getting the key value
					assertEquals("value", jmemcache.get("key"));
				}

				memcached.stop();
			} finally {
				memcachedExe.stop();
				Thread.sleep(500);
			}
		}

	}

	@Test
	public void testStartMemcachedOnNonFreePort()
			throws IOException, InterruptedException {

		MemcachedConfig memcachedConfig = new MemcachedConfig(
				Version.Main.PRODUCTION, 12346);

		final File outer = new File(
				new PropertyOrPlatformTempDir().asFile(), "outer");
		FileUtils.forceMkdir(outer);
		IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
				.defaults(Command.MemcacheD)
				.artifactStore(new ArtifactStoreBuilder()
						.defaultsWithoutCache(Command.MemcacheD)
						.tempDir(new IDirectory() {
							@Override
							public File asFile() {
								return outer;
							}

							@Override
							public boolean isGenerated() {
								return false;
							}
						}))
				.build();

		MemcachedExecutable memcachedExe = MemcachedStarter
				.getInstance(runtimeConfig).prepare(memcachedConfig);
		MemcachedProcess memcached = memcachedExe.start();

		boolean innerMongodCouldNotStart = false;
		Thread.sleep(500);

		MemcachedExecutable innerExe = MemcachedStarter
				.getInstance(new RuntimeConfigBuilder()
						.defaults(Command.MemcacheD).build())
				.prepare(memcachedConfig);
		try {
			MemcachedProcess innerMemcached = innerExe.start();
		} catch (IOException iox) {
			innerMongodCouldNotStart = true;
		} finally {
			innerExe.stop();
			Assert.assertTrue("inner Mongod could not start",
					innerMongodCouldNotStart);
			memcached.stop();
			memcachedExe.stop();
		}
	}
}
