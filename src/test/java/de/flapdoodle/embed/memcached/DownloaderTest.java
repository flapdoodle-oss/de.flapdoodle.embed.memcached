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
package de.flapdoodle.embed.memcached;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.store.IDownloadConfig;
import de.flapdoodle.embed.process.config.store.IDownloadPath;
import de.flapdoodle.embed.process.distribution.BitSize;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.Platform;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.progress.IProgressListener;
import de.flapdoodle.embed.process.store.Downloader;

/**
 * Created with IntelliJ IDEA. User: m.joehren Date: 16.07.12 Time: 22:10 To
 * change this template use File | Settings | File Templates.
 */
public class DownloaderTest {

	private static final int LISTEN_PORT = 17171;
	Server server = null;

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	private IRuntimeConfig rc;
	private IDownloadConfig dc;
	private IProgressListener pl;

	@Before
	public void setUp() throws Exception {
		// start the jetty container
		server = new Server(LISTEN_PORT);
		File myTmpDir = tempDir.newFolder();
		File linuxDir = new File(myTmpDir, "linux");
		linuxDir.mkdir();
		File tempFile = new File(linuxDir, "memcache-1.4.15.tgz");
		tempFile.createNewFile();
		ResourceHandler publicDocs = new ResourceHandler();
		publicDocs.setResourceBase(myTmpDir.getCanonicalPath());

		HandlerList hl = new HandlerList();
		hl.setHandlers(new Handler[] { publicDocs });
		server.setHandler(hl);

		server.start();

		while (true) {
			if (server.isRunning()) {
				break;
			}
			Thread.sleep(100);
		}
		rc = mock(IRuntimeConfig.class);
		dc = mock(IDownloadConfig.class);
		pl = mock(IProgressListener.class);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	// @Test
	public void testDownload() throws Exception {
		initRuntime();
		Distribution d = new Distribution(new GenericVersion("1.4.14"),
				Platform.detect(), BitSize.B64);
		Downloader downloader = new Downloader();
		downloader.download(dc, d);
	}

	private void initRuntime() {
		when(dc.getFileNaming()).thenReturn(new UUIDTempNaming());
		when(dc.getDownloadPath()).thenReturn(new IDownloadPath() {
			@Override
			public String getPath(Distribution distribution) {
				return "http://localhost:" + LISTEN_PORT + "/";
			}
		});
		when(dc.getProgressListener()).thenReturn(pl);
	}

	@Test(expected = Exception.class)
	public void testDownloadShouldThrowExceptionForUnknownVersion()
			throws Exception {
		initRuntime();
		Distribution d = new Distribution(new GenericVersion("3013.1.1"),
				Platform.detect(), BitSize.B64);
		Downloader downloader = new Downloader();
		downloader.download(dc, d);
	}
}
