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

package de.kaiserpfalzedv.commons.core.resources;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PagingTest {
    private static final PagingImpl BASEPAGE = PagingImpl.builder()
            .start(100)
            .size(15)
            .count(15)
            .total(200)
            .build();

    @Test
    public void shouldReturnTheNextPageWhenThereAreEnoughElements() {
        final de.kaiserpfalzedv.commons.api.resources.Paging sut = BASEPAGE;

        final de.kaiserpfalzedv.commons.api.resources.Paging result = sut.nextPage();

        final de.kaiserpfalzedv.commons.api.resources.Paging expected = PagingImpl.builder()
                .start(115)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void shouldReturnIncompletePageWhenThereAreNotEnoughElementsForTheNextPage() {
        final de.kaiserpfalzedv.commons.api.resources.Paging sut = BASEPAGE.toBuilder().total(123).build();

        final de.kaiserpfalzedv.commons.api.resources.Paging result = sut.nextPage();

        final de.kaiserpfalzedv.commons.api.resources.Paging expected = PagingImpl.builder()
                .start(115)
                .size(15)
                .count(8)
                .total(123)
                .build();

        Assertions.assertEquals(expected, result);
    }


    @Test
    public void shouldReturnLastPageWhenRequested() {
        final de.kaiserpfalzedv.commons.api.resources.Paging sut = BASEPAGE.toBuilder().total(123).build();

        final de.kaiserpfalzedv.commons.api.resources.Paging result = sut.lastPage();

        final de.kaiserpfalzedv.commons.api.resources.Paging expected = PagingImpl.builder()
                .start(120)
                .size(15)
                .count(3)
                .total(123)
                .build();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void shouldReturnThePreviousPageWhenThereAreEnoughElements() {
        final de.kaiserpfalzedv.commons.api.resources.Paging sut = BASEPAGE;

        final de.kaiserpfalzedv.commons.api.resources.Paging result = sut.previousPage();

        final de.kaiserpfalzedv.commons.api.resources.Paging expected = PagingImpl.builder()
                .start(85)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }


    @Test
    public void shouldReturnFirstPageWhenRequested() {
        final de.kaiserpfalzedv.commons.api.resources.Paging sut = BASEPAGE.toBuilder().build();

        final de.kaiserpfalzedv.commons.api.resources.Paging result = sut.firstPage();

        final de.kaiserpfalzedv.commons.api.resources.Paging expected = PagingImpl.builder()
                .start(0)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }
    @Test
    public void shouldReturnFirstPageWhenThereAreNotEnoughElementsForThePreviousPage() {
        final de.kaiserpfalzedv.commons.api.resources.Paging sut = BASEPAGE.toBuilder().size(300).build();

        final de.kaiserpfalzedv.commons.api.resources.Paging result = sut.previousPage();

        final de.kaiserpfalzedv.commons.api.resources.Paging expected = PagingImpl.builder()
                .start(0)
                .size(300)
                .count(200)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }
}
