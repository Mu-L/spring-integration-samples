/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.integration.samples.tcpclientserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.util.TestingUtilities;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates the use of a gateway as an entry point into the integration flow.
 * The message generated by the gateway is sent over tcp by the outbound gateway
 * to the inbound gateway. In turn the inbound gateway sends the message to an
 * echo service and the echoed response comes back over tcp and is returned to
 * the test case for verification.
 * <p>
 * This version shows how the conversion service can be used
 * instead of explicit transformers to convert the byte array payloads to
 * Strings.
 *
 * @author Gary Russell
 * @author Gunnar Hillert
 * @author Artme Bilan
 *
 */
@SpringJUnitConfig(locations = "/META-INF/spring/integration/tcpClientServerDemo-conversion-context.xml")
@DirtiesContext
@SpringIntegrationTest(noAutoStartup = "outGateway")
public class TcpClientServerDemoWithConversionServiceTest {

	@Autowired
	SimpleGateway gw;

	@Autowired
	AbstractServerConnectionFactory crLfServer;

	@Autowired
	AbstractClientConnectionFactory client;

	@Autowired
	AbstractEndpoint outGateway;

	@BeforeEach
	public void setup() {
		if (!this.outGateway.isRunning()) {
			TestingUtilities.waitListening(this.crLfServer, 10000L);
			this.client.setPort(this.crLfServer.getPort());
			this.outGateway.start();
		}
	}

	@Test
	public void testHappyDay() {
		String result = gw.send("Hello world!");
		System.out.println(result);
		assertThat(result).isEqualTo("echo:Hello world!");
	}

	@Test
	public void testZeroLength() {
		String result = gw.send("");
		System.out.println(result);
		assertThat(result).isEqualTo("echo:");
	}

	@Test
	public void testFail() {
		String result = gw.send("FAIL");
		System.out.println(result);
		assertThat(result).isEqualTo("FAIL:Failure Demonstration");
	}

}
