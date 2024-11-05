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

package de.kaiserpfalzedv.commons.core.data;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.kaiserpfalzedv.commons.api.data.Paging;

class PagingTest {
    private static final PagingImpl BASE_PAGE = PagingImpl.builder()
            .start(100)
            .size(15)
            .count(15)
            .total(200)
            .build();

    @Test
    void shouldReturnTheNextPageWhenThereAreEnoughElements() {
        final Paging sut = BASE_PAGE;

        final Paging result = sut.nextPage();

        final Paging expected = PagingImpl.builder()
                .start(115)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldReturnIncompletePageWhenThereAreNotEnoughElementsForTheNextPage() {
        final Paging sut = BASE_PAGE.toBuilder().total(123).build();

        final Paging result = sut.nextPage();

        final Paging expected = PagingImpl.builder()
                .start(115)
                .size(15)
                .count(8)
                .total(123)
                .build();

        Assertions.assertEquals(expected, result);
    }


    @Test
    void shouldReturnLastPageWhenRequested() {
        final Paging sut = BASE_PAGE.toBuilder().total(123).build();

        final Paging result = sut.lastPage();

        final Paging expected = PagingImpl.builder()
                .start(120)
                .size(15)
                .count(3)
                .total(123)
                .build();

        Assertions.assertEquals(expected, result);
    }

    @Test
    void shouldReturnThePreviousPageWhenThereAreEnoughElements() {
        final Paging sut = BASE_PAGE;

        final Paging result = sut.previousPage();

        final Paging expected = PagingImpl.builder()
                .start(85)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }


    @Test
    void shouldReturnFirstPageWhenRequested() {
        final Paging sut = BASE_PAGE.toBuilder().build();

        final Paging result = sut.firstPage();

        final Paging expected = PagingImpl.builder()
                .start(0)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }
    @Test
    void shouldReturnFirstPageWhenThereAreNotEnoughElementsForThePreviousPage() {
        final Paging sut = BASE_PAGE.toBuilder().size(300).build();

        final Paging result = sut.previousPage();

        final Paging expected = PagingImpl.builder()
                .start(0)
                .size(300)
                .count(200)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }
}
