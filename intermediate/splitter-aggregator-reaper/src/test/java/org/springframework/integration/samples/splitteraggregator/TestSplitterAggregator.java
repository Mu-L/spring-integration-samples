/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.samples.splitteraggregator;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.samples.splitteraggregator.support.TestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Scatter-Gather
 *
 * @author Christopher Hunt
 * @author Gunnar Hillert
 * @author Artem Bilan
 *
 */
@SpringJUnitConfig(locations = "classpath:META-INF/spring/integration/spring-integration-context.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestSplitterAggregator {

	@Autowired
	SearchRequestor searchRequestor;

	@Autowired
	SearchA searchA;

	@Autowired
	SearchB searchB;

	/**
	 * Test the happy path.
	 */
	@Test
	public void testSearch() {
		CompositeResult result = searchRequestor.search(TestUtils.getCompositeCriteria());
		assertThat(result.getResults()).hasSize(2);
	}

	/**
	 * Test searchA taking longer than we're expecting.
	 */
	@Test
	public void testSearchNoSearchA() {
		searchA.setExecutionTime(6000L);
		CompositeResult result = searchRequestor.search(TestUtils.getCompositeCriteria());
		assertThat(result.getResults()).hasSize(1);
	}

	/**
	 * Test both searchA and searchB taking longer than we're expecting.
	 */
	@Test
	public void testSearchNoSearchB() {
		searchA.setExecutionTime(6000L);
		searchB.setExecutionTime(6000L);
		CompositeResult result = searchRequestor.search(TestUtils.getCompositeCriteria());
		assertThat(result).isNull();
	}

}
