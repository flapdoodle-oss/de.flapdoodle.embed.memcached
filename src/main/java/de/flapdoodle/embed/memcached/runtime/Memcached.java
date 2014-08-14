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
package de.flapdoodle.embed.memcached.runtime;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

/**
 *
 */
public class Memcached {

	protected static Logger logger = Logger.getLogger(Memcached.class
			.getName());

	public static List<String> getCommandLine(MemcachedConfig config,
			IExtractedFileSet memcachedExecutable, File pidFile)
			throws UnknownHostException {
		List<String> ret = new ArrayList<String>();
		ret.addAll(Arrays.asList(memcachedExecutable.executable()
				.getAbsolutePath(),//
				"-vv", // to get status output
						// "-d", // daemon
				"-p", "" + config.net().getPort(), //
				"-P", pidFile.getAbsolutePath() //
				));
		logger.fine("Executable: "
				+ memcachedExecutable.executable().getAbsolutePath());
		return ret;
	}

	public static List<String> enhanceCommandLinePlattformSpecific(
			Distribution distribution, List<String> commands) {
		// do nothing
		return commands;
	}

}
