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

import java.util.logging.Logger;

import de.flapdoodle.embed.memcached.Command;
import de.flapdoodle.embed.process.config.io.ProcessOutput;

/**
 *
 */
public class MemcachedProcessOutputConfig {

	public static ProcessOutput getDefaultInstance() {
		return ProcessOutput.getDefaultInstance("memcached");
	}

	public static ProcessOutput getDefaultInstanceSilent() {
		// don't use default instance which will log to the console and be
		// very
		// chatty
		return ProcessOutput.getDefaultInstanceSilent();
	}

	public static ProcessOutput getInstance(Logger logger) {
		return ProcessOutput.getInstance(Command.MemcacheD.commandName(),
				logger);
	}
}
