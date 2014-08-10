package de.flapdoodle.embed.memcached.config;

import java.util.Arrays;
import java.util.List;

import de.flapdoodle.embed.memcached.distribution.Version;
import de.flapdoodle.embed.process.config.store.ILibraryStore;
import de.flapdoodle.embed.process.distribution.Distribution;

public class MemcachedLibraryStore implements ILibraryStore {

	@Override
	public List<String> getLibrary(Distribution distribution) {
		switch (distribution.getPlatform()) {
		case Windows:
			return asList("libgcc_s_sjlj-1.dll", "mingwm10.dll",
					"pthreadGC2.dll");
		case Linux:
			if (distribution.getVersion().asInDownloadPath()
					.equals(Version.V1_4_20.asInDownloadPath())) {
				return asList("libevent-1.4.so.2");
			}
			return asList("libevent-2.0.so.5");
		case OS_X:
			if (distribution.getVersion().asInDownloadPath()
					.equals(Version.V1_4_20.asInDownloadPath())) {
				return asList("libevent-1.4.so.dylib");
			}
			return asList("libevent-2.0.5.dylib");
		default:
			throw new IllegalArgumentException("Platform not supported: "
					+ distribution.getPlatform());
		}
	}

	static List<String> asList(String... values) {
		return Arrays.asList(values);
	}

}
