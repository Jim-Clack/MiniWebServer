package com.ablestrategies.web;

import junit.framework.TestCase;

public class MapWithDuplicatesTest extends TestCase {

    @SuppressWarnings("all")
    public void testContainsKey() {
        MapWithDuplicates<String, String> mapWithDuplicates = new MapWithDuplicates<>();
        mapWithDuplicates.put("key1", "value1a");
        mapWithDuplicates.put("key1", "value1b");
        mapWithDuplicates.put("key2", "value2a");
        mapWithDuplicates.put("key2", "value2b");
        mapWithDuplicates.put("key3", "value3a");
        assertTrue(mapWithDuplicates.containsKey("key1"));
        assertTrue(mapWithDuplicates.containsKey("key2"));
        assertTrue(mapWithDuplicates.containsKey("key3"));
    }

    public void testContainsValue() {
        MapWithDuplicates<String, String> mapWithDuplicates = new MapWithDuplicates<>();
        mapWithDuplicates.put("key1", "value1a");
        mapWithDuplicates.put("key1", "value1b");
        mapWithDuplicates.put("key2", "value2a");
        mapWithDuplicates.put("key2", "value2b");
        mapWithDuplicates.put("key3", "value3a");
        assertTrue(mapWithDuplicates.containsValue("value1a"));
        assertTrue(mapWithDuplicates.containsValue("value1b"));
        assertTrue(mapWithDuplicates.containsValue("value2a"));
        assertTrue(mapWithDuplicates.containsValue("value2b"));
        assertTrue(mapWithDuplicates.containsValue("value3a"));
    }

    public void testGet() {
        MapWithDuplicates<String, String> mapWithDuplicates = new MapWithDuplicates<>();
        mapWithDuplicates.put("key1", "value1a");
        mapWithDuplicates.put("key1", "value1b");
        mapWithDuplicates.put("key2", "value2a");
        mapWithDuplicates.put("key2", "value2b");
        mapWithDuplicates.put("key3", "value3a");
        assertTrue(mapWithDuplicates.getAll("key1").contains("value1a"));
        assertTrue(mapWithDuplicates.getAll("key1").contains("value1b"));
        assertTrue(mapWithDuplicates.getAll("key2").contains("value2a"));
        assertTrue(mapWithDuplicates.getAll("key2").contains("value2b"));
        assertTrue(mapWithDuplicates.getAll("key3").contains("value3a"));
    }

    public void testGetAll() {
        MapWithDuplicates<String, String> mapWithDuplicates1 = new MapWithDuplicates<>();
        mapWithDuplicates1.put("key1", "value1a");
        mapWithDuplicates1.put("key1", "value1b");
        mapWithDuplicates1.put("key2", "value2a");
        mapWithDuplicates1.put("key2", "value2b");
        mapWithDuplicates1.put("key3", "value3a");
        MapWithDuplicates<String, String> mapWithDuplicates2 = new MapWithDuplicates<>();
        mapWithDuplicates2.put("key3", "value3b");
        mapWithDuplicates2.put("key4", "value4a");
        mapWithDuplicates1.putAll(mapWithDuplicates2);
        assertTrue(mapWithDuplicates1.getAll("key1").contains("value1a"));
        assertTrue(mapWithDuplicates1.getAll("key1").contains("value1b"));
        assertTrue(mapWithDuplicates1.getAll("key2").contains("value2a"));
        assertTrue(mapWithDuplicates1.getAll("key2").contains("value2b"));
        assertTrue(mapWithDuplicates1.getAll("key3").contains("value3a"));
        assertTrue(mapWithDuplicates1.getAll("key3").contains("value3b"));
        assertTrue(mapWithDuplicates1.getAll("key4").contains("value4a"));
    }

    public void testRemove() {
        MapWithDuplicates<String, String> mapWithDuplicates = new MapWithDuplicates<>();
        mapWithDuplicates.put("key1", "value1a");
        mapWithDuplicates.put("key1", "value1b");
        mapWithDuplicates.put("key2", "value2a");
        mapWithDuplicates.put("key2", "value2b");
        mapWithDuplicates.put("key3", "value3a");
        mapWithDuplicates.remove("key2");
        assertTrue(mapWithDuplicates.getAll("key1").contains("value1a"));
        assertTrue(mapWithDuplicates.getAll("key1").contains("value1b"));
        assertNull(mapWithDuplicates.getAll("key2"));
        assertTrue(mapWithDuplicates.getAll("key3").contains("value3a"));
    }

    public void testRemoveValue() {
        MapWithDuplicates<String, String> mapWithDuplicates = new MapWithDuplicates<>();
        mapWithDuplicates.put("key1", "value1a");
        mapWithDuplicates.put("key1", "value1b");
        mapWithDuplicates.put("key2", "value2a");
        mapWithDuplicates.put("key2", "value2b");
        mapWithDuplicates.put("key3", "value3a");
        mapWithDuplicates.removeValue("key2", "value2b");
        assertTrue(mapWithDuplicates.getAll("key1").contains("value1a"));
        assertTrue(mapWithDuplicates.getAll("key1").contains("value1b"));
        assertTrue(mapWithDuplicates.getAll("key2").contains("value2a"));
        assertFalse(mapWithDuplicates.getAll("key2").contains("value2b"));
        assertTrue(mapWithDuplicates.getAll("key3").contains("value3a"));
    }
}