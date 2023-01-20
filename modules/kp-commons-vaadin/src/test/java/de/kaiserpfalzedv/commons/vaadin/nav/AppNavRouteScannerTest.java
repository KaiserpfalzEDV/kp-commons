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

package de.kaiserpfalzedv.commons.vaadin.nav;

import de.kaiserpfalzedv.commons.test.AbstractTestBase;
import io.quarkus.test.junit.QuarkusTest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <p>AppNavRouteScannerTest -- .</p>
 *
 * @author rlichti {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 1.0.0  2023-01-20
 */
@Slf4j
@QuarkusTest
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AppNavRouteScannerTest extends AbstractTestBase {
    private final AppNavRouteScanner sut;

    @PostConstruct
    public void init() {
        setLog(log);
        setTestSuite("app-nav-route-scanner");
    }

    @Test
    void shouldListAllScannedElements() {
        startTest("list-elements");

        Set<Bean<?>> result = sut.getNavigationTargets();
        log.debug("result={}", result);

        assertEquals(4, result.size());
    }

    @Test
    void shouldGenerateTheAppNavItemsForTheTestRoutes() {
        startTest("generate-app-nav-items", TestRoute1.class.getSimpleName(), TestRoute2.class.getSimpleName());

        List<AppNavItem> result = sut.getNavItems();
        log.debug("result={}", result);

        assertEquals(2, result.size());
        assertEquals("Route 2", result.get(0).getLabel());
        assertEquals("view.title.route1", result.get(1).getLabel());
    }
}
