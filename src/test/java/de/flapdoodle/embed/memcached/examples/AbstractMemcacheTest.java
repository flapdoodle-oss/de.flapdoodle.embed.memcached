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
package de.flapdoodle.embed.memcached.examples;

import java.net.InetSocketAddress;

import junit.framework.TestCase;
import net.spy.memcached.MemcachedClient;
import de.flapdoodle.embed.memcached.MemcachedExecutable;
import de.flapdoodle.embed.memcached.MemcachedProcess;
import de.flapdoodle.embed.memcached.MemcachedStarter;
import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.memcached.distribution.Version;

// ->
public abstract class AbstractMemcacheTest extends TestCase {

	private MemcachedExecutable _memcachedExe;
	private MemcachedProcess _memcached;

	private MemcachedClient _jmemcache;

	@Override
	protected void setUp() throws Exception {

		MemcachedStarter runtime = MemcachedStarter.getDefaultInstance();
		_memcachedExe = runtime.prepare(new MemcachedConfig(
				Version.Main.PRODUCTION, 12345));
		_memcached = _memcachedExe.start();

		super.setUp();

		_jmemcache = new MemcachedClient(new InetSocketAddress("localhost",
				12345));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		_memcached.stop();
		_memcachedExe.stop();
	}

	public MemcachedClient getMemcachedClient() {
		return _jmemcache;
	}

}
// <-
