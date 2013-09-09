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
package de.flapdoodle.embed.memcached.runtime;

import junit.framework.TestCase;

//CHECKSTYLE:OFF
public class TestMemcached extends TestCase {

	public void testGetPID() {
		String consoleOutput = "slab class   1: chunk size        96 perslab   10922\n"
				+ "slab class   2: chunk size       120 perslab    8738\n"
				+ "slab class   3: chunk size       152 perslab    6898\n"
				+ "slab class   4: chunk size       192 perslab    5461\n"
				+ "slab class   5: chunk size       240 perslab    4369\n"
				+ "slab class   6: chunk size       304 perslab    3449\n"
				+ "slab class   7: chunk size       384 perslab    2730\n"
				+ "slab class   8: chunk size       480 perslab    2184\n"
				+ "slab class   9: chunk size       600 perslab    1747\n"
				+ "slab class  10: chunk size       752 perslab    1394\n"
				+ "slab class  11: chunk size       944 perslab    1110\n"
				+ "slab class  12: chunk size      1184 perslab     885\n"
				+ "slab class  13: chunk size      1480 perslab     708\n"
				+ "slab class  14: chunk size      1856 perslab     564\n"
				+ "slab class  15: chunk size      2320 perslab     451\n"
				+ "slab class  16: chunk size      2904 perslab     361\n"
				+ "slab class  17: chunk size      3632 perslab     288\n"
				+ "slab class  18: chunk size      4544 perslab     230\n"
				+ "slab class  19: chunk size      5680 perslab     184\n"
				+ "slab class  20: chunk size      7104 perslab     147\n"
				+ "slab class  21: chunk size      8880 perslab     118\n"
				+ "slab class  22: chunk size     11104 perslab      94\n"
				+ "slab class  23: chunk size     13880 perslab      75\n"
				+ "slab class  24: chunk size     17352 perslab      60\n"
				+ "slab class  25: chunk size     21696 perslab      48\n"
				+ "slab class  26: chunk size     27120 perslab      38\n"
				+ "slab class  27: chunk size     33904 perslab      30\n"
				+ "slab class  28: chunk size     42384 perslab      24\n"
				+ "slab class  29: chunk size     52984 perslab      19\n"
				+ "slab class  30: chunk size     66232 perslab      15\n"
				+ "slab class  31: chunk size     82792 perslab      12\n"
				+ "slab class  32: chunk size    103496 perslab      10\n"
				+ "slab class  33: chunk size    129376 perslab       8\n"
				+ "slab class  34: chunk size    161720 perslab       6\n"
				+ "slab class  35: chunk size    202152 perslab       5\n"
				+ "slab class  36: chunk size    252696 perslab       4\n"
				+ "slab class  37: chunk size    315872 perslab       3\n"
				+ "slab class  38: chunk size    394840 perslab       2\n"
				+ "slab class  39: chunk size    493552 perslab       2\n"
				+ "slab class  40: chunk size    616944 perslab       1\n"
				+ "slab class  41: chunk size    771184 perslab       1\n"
				+ "slab class  42: chunk size   1048576 perslab       1\n"
				+ "<26 server listening (auto-negotiate)\n"
				+ "<27 server listening (auto-negotiate)\n"
				+ "<28 send buffer was 212992, now 268435456\n"
				+ "<29 send buffer was 212992, now 268435456\n"
				+ "<28 server listening (udp)\n"
				+ "<28 server listening (udp)\n"
				+ "<29 server listening (udp)\n"
				+ "<28 server listening (udp)\n"
				+ "<29 server listening (udp)\n"
				+ "<28 server listening (udp)\n"
				+ "<29 server listening (udp)\n"
				+ "<29 server listening (udp)\n";

		// assertEquals("PID", 29559,
		// Memcached.getMemcachedProcessId(consoleOutput, -1));
	}

}
