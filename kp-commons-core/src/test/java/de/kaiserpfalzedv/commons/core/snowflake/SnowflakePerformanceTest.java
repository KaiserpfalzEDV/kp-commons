/*
 * Copyright 2021 downgoon, http://downgoon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kaiserpfalzedv.commons.core.snowflake;

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author downgoon {@literal http://downgoon.com}
 * @since 2.0.0  2021-05-24
 */
@Slf4j
public class SnowflakePerformanceTest extends AbstractTestBase {
	public SnowflakePerformanceTest() {
		setTestSuite(getClass().getSimpleName());
		setLog(log);
	}

	@Test
	public void testSingleThread() {
		startTest("single-thread", 1000000, 10000000);

		int n1 = 1000000; // 1百万次
		long[] r1 = runC1N(n1);
		showReport(1, n1, r1);

		int n2 = 10000000; // 1千万次
		long[] r2 = runC1N(n2);
		showReport(1, n2, r2);
	}
	
	@Test
	public void testC10N10w() throws Exception {
		startTest("C10N10w");
		ConcurrentTestFramework ctf = new ConcurrentTestFramework("C10N10w", true);
		final Snowflake snowflake = new Snowflake(2, 5);

		ConcurrentTestFramework.SummaryReport report = ctf.test(10, 100000, snowflake::nextId);
		report.setAttachment(String.format("wait: %d", snowflake.getWaitCount()));
		log.info("C10N10w Report: {}", report);
	}
	
	@Test
	public void testC100N1w() throws Exception {
		startTest("C100N1w");

		ConcurrentTestFramework ctf = new ConcurrentTestFramework("C100N1w", false);
		final Snowflake snowflake = new Snowflake(2, 5);

		ConcurrentTestFramework.SummaryReport report = ctf.test(100, 10000, snowflake::nextId);
		report.setAttachment(String.format("wait: %d", snowflake.getWaitCount()));
		log.info("C100N1w Report: {}", report);
	}
	
	@Test
	public void testC50N100w() throws Exception {
		startTest("C50N100w");
		ConcurrentTestFramework ctf = new ConcurrentTestFramework("C50N100w", false);
		final Snowflake snowflake = new Snowflake(2, 5);

		ConcurrentTestFramework.SummaryReport report = ctf.test(50, 1000000, snowflake::nextId);
		report.setAttachment(String.format("wait: %d", snowflake.getWaitCount()));
		log.info("C50N100w Report: {}", report);
	}

	/**
	 * @return time cost in MS, wait count
	 */
	private long[] runC1N(int n) {
		startTest("C1N");

		Snowflake snowflake = new Snowflake(2, 5);
		long btm = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			snowflake.nextId();
		}
		long etm = System.currentTimeMillis();
		long[] r = new long[2];
		r[0] = etm - btm;
		r[1] = snowflake.getWaitCount();
		return r;
	}

	@SuppressWarnings("SameParameterValue")
	private void showReport(int c, int n, long[] r) {
		long costMS = r[0];
		long qps = (long) (n / (costMS / 1000.0));
		long qpms = n / costMS;
		log.info("C{}N{}: costMS={}, QPS={}, QPMS:={}, wait={}", c, n, costMS, qps, qpms, r[1]);
	}

}
