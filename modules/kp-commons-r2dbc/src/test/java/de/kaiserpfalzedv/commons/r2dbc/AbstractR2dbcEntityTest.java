/*
 * Copyright (c) 2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
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

package de.kaiserpfalzedv.commons.r2dbc;

import lombok.extern.slf4j.XSlf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@XSlf4j
class AbstractR2dbcEntityTest {

    private TestEntity entity;

    @BeforeEach
    void setUp() {
        log.entry();
        
        entity = TestEntity.builder()
                .id(UUID.randomUUID().toString())
                .build();
                
        log.exit();
    }

    @Test
    void shouldInitializeDefaultValues() {
        log.entry();
        
        // Assert
        assertNotNull(entity.getCreated());
        assertNotNull(entity.getModified());
        assertNull(entity.getDeleted());
        assertEquals(ZoneOffset.UTC, entity.getCreated().getOffset());
        assertEquals(ZoneOffset.UTC, entity.getModified().getOffset());
        
        log.exit();
    }

    @Test
    void shouldCloneEntity() throws CloneNotSupportedException {
        log.entry();
        
        // Arrange
        OffsetDateTime deletedTime = OffsetDateTime.now(ZoneOffset.UTC);
        entity.setDeleted(deletedTime);

        // Act
        TestEntity clonedEntity = (TestEntity) entity.clone();

        // Assert
        assertNotNull(clonedEntity);
        assertEquals(entity.getId(), clonedEntity.getId());
        assertEquals(entity.getCreated(), clonedEntity.getCreated());
        assertEquals(entity.getModified(), clonedEntity.getModified());
        assertEquals(entity.getDeleted(), clonedEntity.getDeleted());
        assertNotSame(entity, clonedEntity);
        
        log.exit();
    }

    @Test
    void shouldCreateToString() {
        log.entry();
        
        // Act
        String result = entity.toString();
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains(entity.getId()));
        assertTrue(result.contains(entity.getCreated().toString()));
        assertTrue(result.contains(entity.getModified().toString()));
        
        log.exit();
    }

    @Test
    void shouldTestEquality() {
        log.entry();
        
        // Arrange
        String id = UUID.randomUUID().toString();
        TestEntity entity1 = TestEntity.builder().id(id).build();
        TestEntity entity2 = TestEntity.builder().id(id).build();
        TestEntity entity3 = TestEntity.builder().id(UUID.randomUUID().toString()).build();
        
        // Assert
        assertEquals(entity1, entity2, "Entities with same ID should be equal");
        assertNotEquals(entity1, entity3, "Entities with different IDs should not be equal");
        assertEquals(entity1.hashCode(), entity2.hashCode(), "Hash codes should be equal for equal entities");
        
        log.exit();
    }
    
    /**
     * Concrete implementation of AbstractR2dbcEntity for testing purposes.
     */
    public static class TestEntity extends AbstractR2dbcEntity<String> {
        // Minimale konkrete Implementierung für Testzwecke
        public static TestEntityBuilder builder() {
            return new TestEntityBuilder();
        }
        
        public static class TestEntityBuilder {
            private String id;
            
            public TestEntityBuilder id(String id) {
                this.id = id;
                return this;
            }
            
            public TestEntity build() {
                TestEntity entity = new TestEntity();
                entity.setId(id);
                return entity;
            }
        }
    }
}