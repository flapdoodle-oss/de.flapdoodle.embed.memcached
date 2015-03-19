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
package de.flapdoodle.embed.memcached.distribution;

import de.flapdoodle.embed.process.distribution.IVersion;

/**
 * Memcache Version enum
 */
public enum Version implements IVersion {

	/**
	 * new production release
	 */
	V1_4_22("1.4.22_1"),

	/**
	 * old production releases
	 */
	V1_4_20("1.4.20_2");

	private final String specificVersion;

	Version(String vName) {
		this.specificVersion = vName;
	}

	@Override
	public String asInDownloadPath() {
		return specificVersion;
	}

	@Override
	public String toString() {
		return "Version{" + specificVersion + '}';
	}

	public static enum Main implements IVersion {
		/**
		 * current production release
		 */
		V1_4(V1_4_22),

		/**
		 * old production release
		 */
		V1_4_OLD(V1_4_20),

		PRODUCTION(V1_4),

		@Deprecated
		DEPRECATED(V1_4_OLD);

		private final IVersion _latest;

		Main(IVersion latest) {
			_latest = latest;
		}

		@Override
		public String asInDownloadPath() {
			return _latest.asInDownloadPath();
		}
	}
}
