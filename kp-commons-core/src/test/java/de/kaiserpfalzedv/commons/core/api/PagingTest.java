/*
 * Copyright (c) 2023. Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.core.api;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PagingTest {
    private static final Paging BASEPAGE = Paging.builder()
            .start(100)
            .size(15)
            .count(15)
            .total(200)
            .build();

    @Test
    public void shouldReturnTheNextPageWhenThereAreEnoughElements() {
        Paging sut = BASEPAGE;

        Paging result = sut.nextPage();

        Paging expected = Paging.builder()
                .start(115)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void shouldReturnIncompletePageWhenThereAreNotEnoughElementsForTheNextPage() {
        Paging sut = BASEPAGE.toBuilder().total(123).build();

        Paging result = sut.nextPage();

        Paging expected = Paging.builder()
                .start(115)
                .size(15)
                .count(8)
                .total(123)
                .build();

        Assertions.assertEquals(expected, result);
    }


    @Test
    public void shouldReturnLastPageWhenRequested() {
        Paging sut = BASEPAGE.toBuilder().total(123).build();

        Paging result = sut.lastPage();

        Paging expected = Paging.builder()
                .start(120)
                .size(15)
                .count(3)
                .total(123)
                .build();

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void shouldReturnThePreviousPageWhenThereAreEnoughElements() {
        Paging sut = BASEPAGE;

        Paging result = sut.previousPage();

        Paging expected = Paging.builder()
                .start(85)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }


    @Test
    public void shouldReturnFirstPageWhenRequested() {
        Paging sut = BASEPAGE.toBuilder().build();

        Paging result = sut.firstPage();

        Paging expected = Paging.builder()
                .start(0)
                .size(15)
                .count(15)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }
    @Test
    public void shouldReturnFirstPageWhenThereAreNotEnoughElementsForThePreviousPage() {
        Paging sut = BASEPAGE.toBuilder().size(300).build();

        Paging result = sut.previousPage();

        Paging expected = Paging.builder()
                .start(0)
                .size(300)
                .count(200)
                .total(200)
                .build();

        Assertions.assertEquals(expected, result);
    }
}
