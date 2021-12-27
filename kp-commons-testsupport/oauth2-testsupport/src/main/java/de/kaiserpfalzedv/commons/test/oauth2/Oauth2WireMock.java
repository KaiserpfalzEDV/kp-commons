/*
 * Copyright (c) 2021 Kaiserpfalz EDV-Service, Roland T. Lichti.
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

package de.kaiserpfalzedv.commons.test.oauth2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * This is the wiremock for oauth2 services for test and dev.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2021-05-24
 */
@Slf4j
public class Oauth2WireMock implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        WireMock.stubFor(
                WireMock.post("/introspect")
                        .willReturn(
                                WireMock.aResponse()
                                .withBody(
                                        "{\"active\":true,\"scope\":\"openid,guilds\",\"username\":\"klenkes74\",\"iat\":1562315654,\"exp\":1562317454,\"expires_in\":1458,\"client_id\":\"my_client_id\"}"
                                )
                        )
        );

        String url = wireMockServer.baseUrl() + "/introspect";
        log.info("Oauth2 server wiremock started on '{}/introspect'", url);
        return Map.of(
                "quarkus.oauth2.introspection-url", url,
                "quarkus.oidc.authorization-path", wireMockServer.baseUrl() + "/authorization",
                "quarkus.oidc.user-info-path", wireMockServer.baseUrl() + "/user-info",
                "quarkus.oidc.introspection-path", url
        );
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();

            log.info("Oauth2 server wiremock '{}/introspect' stopped.", wireMockServer.baseUrl());
        }
    }

}
