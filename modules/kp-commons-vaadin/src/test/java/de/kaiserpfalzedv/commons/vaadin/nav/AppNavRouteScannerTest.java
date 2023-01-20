/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
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
