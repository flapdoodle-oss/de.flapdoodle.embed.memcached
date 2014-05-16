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
package de.flapdoodle.embed.memcached;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.flapdoodle.embed.memcached.config.MemcachedConfig;
import de.flapdoodle.embed.memcached.config.SupportConfig;
import de.flapdoodle.embed.memcached.runtime.Memcached;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.LogWatchStreamProcessor;
import de.flapdoodle.embed.process.io.Processors;
import de.flapdoodle.embed.process.io.StreamToLineProcessor;
import de.flapdoodle.embed.process.runtime.AbstractProcess;
import de.flapdoodle.embed.process.runtime.ProcessControl;
import de.flapdoodle.embed.process.runtime.Processes;

public class MemcachedProcess
		extends
		AbstractProcess<MemcachedConfig, MemcachedExecutable, MemcachedProcess> {

	private static Logger logger = Logger.getLogger(MemcachedProcess.class
			.getName());

	boolean stopped = false;

	public MemcachedProcess(Distribution distribution,
			MemcachedConfig config, IRuntimeConfig runtimeConfig,
			MemcachedExecutable memcachedExecutable) throws IOException {
		super(distribution, config, runtimeConfig, memcachedExecutable);
	}

	protected Set<String> knownFailureMessages() {
		HashSet<String> ret = new HashSet<String>();
		ret.add("failed to listen on TCP port");
		ret.add("Address already in use");
		return ret;
	}

	@Override
	protected void stopInternal() {

		synchronized (this) {
			if (!stopped) {

				stopped = true;

				logger.info("try to stop memcached");
				if (!sendKillToProcess()) {
					logger.warning("could not kill memcached, try next");
					if (!sendTermToProcess()) {
						logger.warning("could not term memcached, try next");
						if (!tryKillToProcess()) {
							logger.warning("could not stop memcached the third time, try one last thing");
						}
					}
				}

				stopProcess();
			}
		}
	}

	@Override
	protected void onBeforeProcess(IRuntimeConfig runtimeConfig)
			throws IOException {
		super.onBeforeProcess(runtimeConfig);

		MemcachedConfig config = getConfig();
	}

	@Override
	protected List<String> getCommandLine(Distribution distribution,
			MemcachedConfig config, IExtractedFileSet exe)
			throws IOException {
		return Memcached.enhanceCommandLinePlattformSpecific(distribution,
				Memcached.getCommandLine(getConfig(), exe, pidFile()));
	}

	@Override
	protected Map<String, String> getEnvironment(Distribution distribution,
			MemcachedConfig config, IExtractedFileSet exe) {
		HashMap<String, String> environment = new HashMap<String, String>();
		// set LD_LIBRARY_PATH
		if (distribution.getPlatform() == Platform.Linux) {
			environment.put("LD_LIBRARY_PATH", exe.executable()
					.getParent());
		} else if (distribution.getPlatform() == Platform.OS_X) {
			environment.put("DYLD_LIBRARY_PATH", exe.executable()
					.getParent());
		}
		return environment;
	}

	@Override
	protected final void onAfterProcessStart(ProcessControl process,
			IRuntimeConfig runtimeConfig) throws IOException {

		ProcessOutput outputConfig = runtimeConfig.getProcessOutput();
		// memcached prints all status messages on error console, and only
		// if
		// started with -vv
		LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
				"server listening", knownFailureMessages(),
				StreamToLineProcessor.wrap(outputConfig.getError()));
		Processors.connect(process.getError(), logWatch);
		logWatch.waitForResult(getConfig().timeout().getStartupTimeout());

		if (!logWatch.isInitWithSuccess()) {
			throw new IOException("Could not start process:"
					+ logWatch.getOutput());
		}

		if (Distribution.detectFor(getConfig().version()).getPlatform() == Platform.Windows) {
			// On windows, process won't be writing a pid file. Sigh. Use
			// some
			// force to find out the pid anyways. This works pretty well
			// on Unix
			// (won't help us much here..) and needs Sigar lib on Windows.
			setProcessId(process.getPid());
		} else {
			setProcessId(getPidFromFile(pidFile()));
		}
	}

	@Override
	protected void cleanupInternal() {
	}

	public static void stopStaleProcess(File pidFile, IVersion version)
			throws IOException {
		try {
			int pid = getPidFromFile(pidFile);
			Platform platform = Distribution.detectFor(version)
					.getPlatform();
			synchronized (MemcachedProcess.class) {
				logger.info("try to stop memcached");

				if (!Processes.killProcess(new SupportConfig(
						Command.MemcacheD), platform,
						StreamToLineProcessor.wrap(Processors
								.console()), pid)) {
					logger.warning("could not kill memcached, try next");
					if (!Processes.termProcess(new SupportConfig(
							Command.MemcacheD), platform,
							StreamToLineProcessor.wrap(Processors
									.console()), pid)) {
						logger.warning("could not term memcached, try next");
						if (!Processes
								.tryKillProcess(
										new SupportConfig(
												Command.MemcacheD),
										platform,
										StreamToLineProcessor
												.wrap(Processors
														.console()),
										pid)) {
							logger.warning("could not stop memcached the third time, try one last thing");
						}
					}
				}
			}
		} catch (IOException e) {
			// will throw if there is no pid file, ignore in this case
		}

	}
}
