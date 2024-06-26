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

import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class CacheMapGetTest {
    private Object key;
    private String keyType;
    private boolean existingKey;
    private CacheMap cacheMap;
    private Object output;
    private Integer dummyValue = 5;
    private boolean inSoftMap;
    private int softMapSize = 512;

    private static final String NULL = "null";
    private static final String VALID = "valid";
    private static final String INVALID = "invalid";

    public CacheMapGetTest(String keyType, boolean existingKey, boolean inSoftMap, Object output) {
        this.keyType = keyType;
        this.existingKey = existingKey;
        this.output = output;
        this.inSoftMap = inSoftMap;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {NULL, false, false, null},
                {NULL, true, false, 5},
                {VALID, true, false, 5},
                {VALID, false, false, null},
                {INVALID, true, false, null},
                {INVALID, false, false, null},
                // Test cases added after JaCoCo results
                {NULL, false, true, 5},
                {VALID, true, true, 5},
                {VALID, false, true, 5},
                {INVALID, true, true, null},
                {INVALID, false, true, null}
        });
    }

    @Before
    public void setUp() {
        cacheMap = spy(new CacheMap());

        setParam(keyType);

        if (existingKey)
            cacheMap.put(key, dummyValue);

        if (inSoftMap) {
            cacheMap.setSoftReferenceSize(softMapSize);
            cacheMap.put(cacheMap.softMap, key, dummyValue);
        }
    }

    @Test
    public void test() {
        Object res = cacheMap.get(key);

        if (output != null && keyType.equals(VALID) || keyType.equals(NULL)) {
            Assert.assertEquals(output, res);
        } else {
            Assert.assertNull(res);
        }

        /* Some mutations killed */
        if (existingKey && inSoftMap && !keyType.equals(INVALID)) {
            verify(cacheMap, times(2)).put(key, dummyValue);
        }
    }

    @After
    public void tearDown() {
        cacheMap.clear();
    }

    private void setParam(String param) {
        switch (param) {
            case NULL:
                key = null;
                break;
            case VALID:
                key = new Object();
                break;
            case INVALID:
                key = new InvalidKeyValue();
                break;
        }
    }
}
