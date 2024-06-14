package org.apache.openjpa.util;

import org.apache.openjpa.util.testUtil.InvalidKeyValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class CacheMapPutTest {
    private Object key;
    private Object value;
    private final boolean existingKey;
    private CacheMap cacheMap;
    private final Object output;
    private Integer dummyValueOld = 5;
    private Integer dummyValueNew = 6;
    public String keyType;
    public String valueType;
    private boolean maxSize;
    private boolean pinnedMap;

    private static final String NULL = "null";
    private static final String VALID = "valid";
    private static final String INVALID = "invalid";

    public CacheMapPutTest(String keyType, String valueType, boolean existingKey, boolean maxSize, boolean pinnedMap, Object output) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.existingKey = existingKey;
        this.output = output;
        this.maxSize = maxSize;
        this.pinnedMap = pinnedMap;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {NULL, NULL, false, false, false, null},
                {NULL, NULL, true, false, false, null},
                {NULL, VALID, true, false, false, 5},
                {NULL, VALID, false, false, false, null},
                {NULL, INVALID, false, false, false, null},
                {NULL, INVALID, true, false, false, null},
                {VALID, VALID, true, false, false, 5},
                {VALID, VALID, false, false, false, null},
                {VALID, NULL, true, false, false, null},
                {VALID, NULL, false, false, false, null},
                {VALID, INVALID, true, false, false, new InvalidKeyValue()}, // return invalid object
                {VALID, INVALID, false, false, false, null},
                {INVALID, VALID, true, false, false, 5}, // invalid key
                {INVALID, VALID, false, false, false, null},
                {INVALID, NULL, true, false, false, null},
                {INVALID, NULL, false, false, false, null},
                {INVALID, INVALID, true, false, false, null},
                {INVALID, INVALID, false, false, false, null},
                // Test cases added after JaCoCo results
                {VALID, INVALID, true, true, false, null},  // if (cacheMap.getMaxSize() == 0) return null;
                {VALID, VALID, true, true, false, null},
                {VALID, VALID, false, false, true, 5},
                {VALID, INVALID, false, false, true, new InvalidKeyValue()},
                {VALID, NULL, false, false, true, null},
        });
    }

    @Before
    public void setUp() {
        cacheMap = spy(new CacheMap());

        setKey(keyType);
        setValue(valueType);

        if (maxSize) {
            cacheMap.cacheMap.setMaxSize(0);
        } else if (existingKey) {
            cacheMap.put(key, value);
        } else if (pinnedMap) {
            cacheMap.put(cacheMap.pinnedMap, key, value);
        }

    }

    @Test
    public void test() {
        Object newValue = null;
        if (!Objects.equals(valueType, NULL))
            if (Objects.equals(valueType, VALID))
                newValue = dummyValueNew;
            else
                newValue = new InvalidKeyValue();

        if (pinnedMap) {
            /* Clear cacheMap to ensure get(key) from pinnedMap */
            cacheMap.cacheMap.clear();
        }

        Object res = cacheMap.put(key, newValue);  // res is the output that we want to check
        Object checkGet = cacheMap.get(key);

        if (!existingKey && !pinnedMap)
            System.out.println(res);

        /* check put return (test goal) */
        if (output != null) {
            Assert.assertEquals(output, res);
        } else {
            /* check when the output is null:
             * 1) key is invalid
             * 2) no existing key */
            Assert.assertNull(res);
        }

        /* check get return */
        if (!valueType.equals(NULL) && !maxSize)
            Assert.assertNotNull(checkGet);
        else
            Assert.assertNull(checkGet);

        /* Some mutations killed */
        if (pinnedMap) {
            if (valueType.equals(NULL)) {   // mutation on line 393
                verify(cacheMap).entryAdded(key, newValue);
            } else {    // mutations on line 391-395-396
                verify(cacheMap).entryRemoved(key, value, false);
                verify(cacheMap).entryAdded(key, newValue);
            }
            verify(cacheMap).writeUnlock();
        } else if (!maxSize && existingKey && valueType.equals(VALID)) {  // mutations on line 417-418
            verify(cacheMap).entryRemoved(key, value, false);
            verify(cacheMap).entryAdded(key, newValue);
            verify(cacheMap, times(2)).writeUnlock();
        } else if (existingKey && valueType.equals(NULL)) { // mutation on line 411
            verify(cacheMap, times(2)).entryAdded(key, newValue);
        }
    }

    @After
    public void tearDown() {
        cacheMap.clear();
    }

    private void setKey(String keyType) {
        switch (keyType) {
            case NULL:
                key = null;
                break;
            case VALID:
                key = new Object();
                break;
            case INVALID:
                key = this.output;
                break;
        }
    }

    private void setValue(String valueType) {
        switch (valueType) {
            case NULL:
                value = null;
                break;
            case VALID:
                value = dummyValueOld;
                break;
            case INVALID:
                value = this.output;
                break;
        }
    }
}
