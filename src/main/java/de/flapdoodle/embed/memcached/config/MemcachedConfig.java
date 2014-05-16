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
package de.flapdoodle.embed.memcached.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import de.flapdoodle.embed.memcached.Command;
import de.flapdoodle.embed.process.config.ExecutableProcessConfig;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.runtime.Network;

public class MemcachedConfig extends ExecutableProcessConfig {

	public static MemcachedConfig getConfigInstance(IVersion version,
			Net network) {
		return new MemcachedConfig(version, network, new Storage(),
				new Timeout());
	}

	protected final Net network;
	protected final Storage storage;
	protected final Timeout timeout;

	public MemcachedConfig(IVersion version, Net networt, Storage storage,
			Timeout timeout) {
		super(version, new SupportConfig(Command.MemcacheD));
		this.network = networt;
		this.timeout = timeout;
		this.storage = storage;
	}

	public MemcachedConfig(IVersion version) throws UnknownHostException,
			IOException {
		this(version, new Net(), new Storage(), new Timeout());
	}

	public MemcachedConfig(IVersion version, int port) {
		this(version, new Net(port), new Storage(), new Timeout());
	}

	public Net net() {
		return network;
	}

	public Timeout timeout() {
		return timeout;
	}

	public Storage getStorage() {
		return storage;
	}

	public static class Storage {

		private final String pidFile;

		public Storage() {
			this(null);
		}

		public Storage(String pidFile) {
			this.pidFile = pidFile;
		}

		public String getPidFile() {
			return pidFile;
		}

	}

	public static class Net {

		private final int port;

		public Net() throws UnknownHostException, IOException {
			this(Network.getFreeServerPort());
		}

		public Net(int port) {
			this.port = port;
		}

		public int getPort() {
			return port;
		}

		public InetAddress getServerAddress() throws UnknownHostException {
			return Network.getLocalHost();
		}
	}

	public static class Timeout {

		private final long startupTimeout;

		public Timeout() {
			this(5000);
		}

		public Timeout(long startupTimeout) {
			this.startupTimeout = startupTimeout;
		}

		public long getStartupTimeout() {
			return startupTimeout;
		}
	}

}
