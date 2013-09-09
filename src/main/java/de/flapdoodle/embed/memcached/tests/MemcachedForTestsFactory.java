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
package de.flapdoodle.embed.memcached.tests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import net.spy.memcached.MemcachedClient;
import de.flapdoodle.embed.memcached.Command;
import de.flapdoodle.embed.memcached.MemcachedExecutable;
import de.flapdoodle.embed.memcached.MemcachedProcess;
import de.flapdoodle.embed.memcached.MemcachedStarter;
import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.memcached.config.MemcachedConfig.Net;
import de.flapdoodle.embed.memcached.config.MemcachedConfig.Storage;
import de.flapdoodle.embed.memcached.config.MemcachedConfig.Timeout;
import de.flapdoodle.embed.memcached.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.memcached.distribution.Version;
import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * This class encapsulates everything that would be needed to do embedded
 * Memcache testing.
 */
public class MemcachedForTestsFactory {

	private static Logger logger = Logger
			.getLogger(MemcachedForTestsFactory.class.getName());

	public static MemcachedForTestsFactory with(final IVersion version)
			throws IOException {
		return new MemcachedForTestsFactory(version);
	}

	private final MemcachedExecutable memcachedExecutable;

	private final MemcachedProcess memcachedProcess;

	/**
	 * Create the testing utility using the latest production version of
	 * Memcache.
	 * 
	 * @throws IOException
	 */
	public MemcachedForTestsFactory() throws IOException {
		this(Version.Main.PRODUCTION);
	}

	/**
	 * Create the testing utility using the specified version of MongoDB.
	 * 
	 * @param version
	 *              version of MongoDB.
	 */
	public MemcachedForTestsFactory(final IVersion version)
			throws IOException {

		final MemcachedStarter runtime = MemcachedStarter
				.getInstance(new RuntimeConfigBuilder()
						.defaultsWithLogger(Command.MemcacheD,
								logger).build());
		memcachedExecutable = runtime.prepare(newMemcachedConfig(version));
		memcachedProcess = memcachedExecutable.start();

	}

	protected MemcachedConfig newMemcachedConfig(final IVersion version)
			throws UnknownHostException, IOException {
		return new MemcachedConfig(version, new Net(), new Storage(),
				new Timeout());
	}

	/**
	 * Creates a new Memcache connection.
	 * 
	 * @throws IOException
	 */
	public MemcachedClient newMemcachedClient() throws IOException {
		return new MemcachedClient(new InetSocketAddress(memcachedProcess
				.getConfig().net().getServerAddress()
				.getCanonicalHostName(), memcachedProcess.getConfig()
				.net().getPort()));
	}

	/**
	 * Cleans up the resources created by the utility.
	 */
	public void shutdown() {
		memcachedProcess.stop();
		memcachedExecutable.stop();
	}
}
