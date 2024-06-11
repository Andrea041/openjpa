package org.apache.openjpa.util;

import org.apache.openjpa.util.testUtil.ProxyableInstance;
import org.apache.openjpa.util.testUtil.NonProxyableIstance;
import org.apache.openjpa.util.testUtil.ObjectType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class ProxyManagerImplCopyTest {
    private ProxyManagerImpl proxyManager;
    private final Object obj;
    private final ObjectType objectInstance;

    public ProxyManagerImplCopyTest(ObjectType objectType) throws Exception {
        this.obj = generateObj(objectType);
        this.objectInstance = objectType;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {ObjectType.NULL},
                {ObjectType.PROXYABLE},
                {ObjectType.NON_PROXYABLE}
        });
    }

    @Before
    public void setUp() {
        proxyManager = spy(new ProxyManagerImpl());
        proxyManager.setUnproxyable(NonProxyableIstance.class.getName());   // set this type of class as not proxyable
    }


    @Test
    public void test() {
        Object output = proxyManager.copyCustom(obj);

        /* Check integrity of copy */
        checkCopy(output);
    }

    private Object generateObj(ObjectType objectType) throws Exception {
        switch (objectType) {
            case NULL:
                return null;
            case PROXYABLE:
                return new ProxyableInstance();
            case NON_PROXYABLE:
                return new NonProxyableIstance("Apple", "iPhone12");
            default:
                throw new Exception("Invalid argument");
        }
    }

    private void checkCopy(Object output) {
        switch (objectInstance) {
            case NULL:
            case NON_PROXYABLE:
                Assert.assertNull(output);
                break;
            case PROXYABLE:
                assert output != null;
                /* check that the state of the proxied object (output) is the same of the original obj */
                Assert.assertEquals(((ProxyableInstance) output).getDummy(), ((ProxyableInstance) obj).getDummy());
                break;
        }
    }

    @After
    public void tearDown() {
        proxyManager = null;
    }
}

