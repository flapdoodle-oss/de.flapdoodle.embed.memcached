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

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;

import net.spy.memcached.MemcachedClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.memcached.distribution.Version;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * Test whether a race condition occurs between setup and tear down of setting
 * up and closing a memcache process.
 * <p/>
 * This test will run a long time based on the download process for all memcache
 * versions.
 * 
 * @author m.joehren
 */
@RunWith(value = Parameterized.class)
public class MemcachedExampleAllVersionsTest {
	@Parameters
	public static Collection<Object[]> data() {
		Collection<Object[]> result = new ArrayList<Object[]>();
		for (IVersion version : Versions
				.testableVersions(Version.Main.class)) {
			if (!version.equals(Version.Main.V1_4)) {
				result.add(new Object[] { version });
			}
		}
		return result;
	}

	private static final int PORT = 12345;
	private final IVersion memcacheVersion;
	private MemcachedExecutable memcachedExe;
	private MemcachedProcess memcached;
	private MemcachedClient jmemcache;

	public MemcachedExampleAllVersionsTest(IVersion v) {
		this.memcacheVersion = v;
	}

	@Before
	public void setUp() throws Exception {

		MemcachedStarter runtime = MemcachedStarter.getDefaultInstance();
		memcachedExe = runtime.prepare(new MemcachedConfig(
				this.memcacheVersion, PORT));
		memcached = memcachedExe.start();

		// Connecting to Memcache on localhost
		jmemcache = new MemcachedClient(new InetSocketAddress("localhost",
				PORT));
	}

	@After
	public void tearDown() throws Exception {

		memcached.stop();
		memcachedExe.stop();
	}

	public MemcachedClient getMemcache() {
		return jmemcache;
	}

	@Test
	public void testInsert1() {
		// adding a new key
		jmemcache.add("key", 5, "value");
		// getting the key value
		assertEquals("value", jmemcache.get("key"));
	}

}
