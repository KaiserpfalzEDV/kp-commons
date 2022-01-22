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
import io.quarkus.test.junit.QuarkusTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author downgoon {@literal http://downgoon.com}
 * @since 2.0.0  2021-05-24
 */
@QuarkusTest
@Slf4j
public class SnowflakeTest extends AbstractTestBase {
	@PostConstruct
	void init() {
		setTestSuite(getClass().getSimpleName());
		setLog(log);
	}

	@Test
	public void testNextIdAndParse() throws Exception {
		startTest("next-id-and-parse");

		long beginTimeStamp = System.currentTimeMillis();
		Snowflake snowflake = new Snowflake(3, 16);

		// gen id and parse it
		long id = snowflake.nextId();
		long[] arr = snowflake.parseId(id);
		log.debug(snowflake.formatId(id));

		assertTrue(arr[0] >= beginTimeStamp);
		assertEquals(3, arr[1]); // datacenterId
		assertEquals(16, arr[2]); // workerId
		assertEquals(0, arr[3]); // sequenceId

		// gen two ids in different MS
		long id2 = snowflake.nextId();
		assertNotEquals(id2, id);
		log.debug(snowflake.formatId(id2));
		 
		Thread.sleep(1); // wait one ms 
		long id3 = snowflake.nextId();
		long[] arr3 = snowflake.parseId(id3);
		log.debug(snowflake.formatId(id3));
		assertTrue(arr3[0] > arr[0]);

		// gen two ids in the same MS
		long[] ids = new long[2];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = snowflake.nextId();
		}
		log.debug(snowflake.formatId(ids[0]));
		log.debug(snowflake.formatId(ids[1]));
		assertNotEquals(ids[1], ids[0]);
		long[] arr_ids0 = snowflake.parseId(ids[0]);
		long[] arr_ids1 = snowflake.parseId(ids[1]);
		assertEquals(arr_ids0[0], arr_ids1[0]);
		assertEquals(arr_ids0[3], arr_ids1[3] - 1);
	}
	
}
