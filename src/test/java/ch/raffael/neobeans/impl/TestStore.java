package ch.raffael.neobeans.impl;

import java.net.URL;

import org.testng.annotations.*;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;

import ch.raffael.neobeans.NeoBeanStore;
import ch.raffael.neobeans.NodeKey;

import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestStore extends Neo4jTest {

    private NodeKey foobarKey;

    @BeforeClass
    public void setupMapping() throws Exception {
        BeanMapping mapping = new BeanMapping(true, TestBean.class, new BeanPropertyMapping(
                beanStore.database(), NeoBeanStore.PROPERTY_KEY, null, null,
                TestBean.class.getMethod("getKey"), TestBean.class.getMethod("setKey", NodeKey.class)));
        mapping.addPropertyMapping(new BeanPropertyMapping(
                beanStore.database(), "name", null, null,
                TestBean.class.getMethod("getName"), TestBean.class.getMethod("setName", String.class)));
        mapping.addPropertyMapping(new BeanPropertyMapping(
                beanStore.database(), "homepage", TestBean.URL_CONVERTER, null,
                TestBean.class.getMethod("getHomepage"), TestBean.class.getMethod("setHomepage", URL.class)));
        beanStore.setMapping(TestBean.class, mapping);
    }

    @AfterClass(alwaysRun = true)
    public void clearMapping() {
        beanStore.removeMapping(TestBean.class);
    }

    @Test
    public void testCreate() throws Exception {
        TestBean bean = new TestBean();
        bean.setKey(NodeKey.create());
        bean.setName("FooBar");
        Transaction tx = beginTx();
        try {
            beanStore.store(bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        assertNotNull(bean.getKey().getId(), "Node ID is null");
        foobarKey = bean.getKey();
        tx = beginTx();
        try {
            Node node = beanStore.database().getNodeById(bean.getKey().getId());
            assertEquals(node.getProperty(NeoBeanStore.PROPERTY_TYPE), TestBean.class.getName(), "Type mismatch");
            assertEquals(node.getProperty(NeoBeanStore.PROPERTY_KEY), bean.getKey().getKey(), "Key mismatch");
            assertEquals(node.getProperty("name"), bean.getName(), "Name mismatch");
            assertNull(node.getProperty("homepage", null), "Name mismatch");
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    @Test(dependsOnMethods = "testCreate")
    public void testUpdate() throws Exception {
        TestBean bean = new TestBean();
        bean.setKey(foobarKey);
        bean.setName("BarFoo");
        Transaction tx = beginTx();
        try {
            beanStore.store(bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            Node node = beanStore.database().getNodeById(bean.getKey().getId());
            assertEquals(node.getProperty("name"), "BarFoo", "Name mismatch");
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    @Test(expectedExceptions = NotFoundException.class,
          dependsOnMethods = "testCreate")
    public void testNotFoundById() throws Exception {
        TestBean bean = new TestBean();
        bean.setKey(NodeKey.arbitrary(Long.MAX_VALUE, foobarKey.getKey())); // should be sufficiently improbable it's just a test
        bean.setName("Fail");
        Transaction tx = beginTx();
        try {
            beanStore.store(bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    @Test(expectedExceptions = NotFoundException.class,
          dependsOnMethods = "testCreate")
    public void testNotFoundByKey() throws Exception {
        TestBean bean = new TestBean();
        bean.setKey(NodeKey.arbitrary(foobarKey.getId(), "abcd"));
        bean.setName("Fail");
        Transaction tx = beginTx();
        try {
            beanStore.store(bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    // FIXME: Not found by type
}
