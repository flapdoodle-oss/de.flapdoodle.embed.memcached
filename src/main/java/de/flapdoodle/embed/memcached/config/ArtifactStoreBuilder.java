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

import de.flapdoodle.embed.memcached.Command;
import de.flapdoodle.embed.process.config.store.ILibraryStore;
import de.flapdoodle.embed.process.config.store.LibraryStoreBuilder;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir;

public class ArtifactStoreBuilder extends
		de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

	public ArtifactStoreBuilder defaults(Command command) {
		tempDir().setDefault(new PropertyOrPlatformTempDir());
		executableNaming().setDefault(new UUIDTempNaming());
		download().setDefault(
				new DownloadConfigBuilder().defaultsForCommand(command)
						.build());
		libraries().setDefault(libraryStore());
		return this;
	}

	private ILibraryStore libraryStore() {
		LibraryStoreBuilder libraryStoreBuilder = new LibraryStoreBuilder()
				.defaults();

		libraryStoreBuilder.setLibraries(Platform.Windows,
				new String[] { "libgcc_s_sjlj-1.dll", "mingwm10.dll",
						"pthreadGC2.dll" });
		libraryStoreBuilder.setLibraries(Platform.Linux,
				new String[] { "libevent-2.0.so.5" });
		libraryStoreBuilder.setLibraries(Platform.OS_X,
				new String[] { "libevent-2.0.5.dylib" });
		return libraryStoreBuilder.build();
	}

	public ArtifactStoreBuilder defaultsWithoutCache(Command command) {
		tempDir().setDefault(new PropertyOrPlatformTempDir());
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
