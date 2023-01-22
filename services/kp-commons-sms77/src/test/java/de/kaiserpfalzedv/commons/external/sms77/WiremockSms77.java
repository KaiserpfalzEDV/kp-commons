/*
 * Copyright (c) 2023. Roland T. Lichti, Kaiserpfalz EDV-Service.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.external.sms77;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class WiremockSms77 implements QuarkusTestResourceLifecycleManager {

    private WireMockServer server;

    @Override
    public Map<String, String> start() {
        server = new WireMockServer();
        server.start();

        log.info("Stubs registered. url='{}', stubs={}", server.baseUrl(), server.listAllStubMappings().getMappings());

        return Map.of(
                "quarkus.rest-client.\"de.kaiserpfalzedv.commons.external.sms77.client.Sms77Client\".url", server.baseUrl(),
                "quarkus.rest-client.\"de.kaiserpfalzedv.commons.external.sms77.client.Sms77Client\".scope", "javax.inject.Singleton"
        );
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
