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
package de.flapdoodle.embed.memcached.config;

import de.flapdoodle.embed.memcached.Command;
import de.flapdoodle.embed.process.config.store.ILibraryStore;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.PropertyOrTempDirInPlatformTempDir;

public class ArtifactStoreBuilder extends
		de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

	public ArtifactStoreBuilder defaults(Command command) {
		tempDir().setDefault(new PropertyOrTempDirInPlatformTempDir());
		executableNaming().setDefault(new UUIDTempNaming());
		download().setDefault(
				new DownloadConfigBuilder().defaultsForCommand(command)
						.build());
		libraries().setDefault(libraryStore());
		return this;
	}

	public static ILibraryStore libraryStore() {
		return new MemcachedLibraryStore();
	}

	public ArtifactStoreBuilder defaultsWithoutCache(Command command) {
		tempDir().setDefault(new PropertyOrTempDirInPlatformTempDir());
		executableNaming().setDefault(new UUIDTempNaming());
		download().setDefault(
				new DownloadConfigBuilder().defaultsForCommand(command)
						.build());
		libraries().setDefault(libraryStore());
		// disable caching
		useCache().setDefault(false);
		return this;
	}
}
