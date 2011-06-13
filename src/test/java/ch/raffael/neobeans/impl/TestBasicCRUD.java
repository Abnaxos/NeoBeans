package ch.raffael.neobeans.impl;

import java.net.URL;
import java.util.UUID;

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
public class TestBasicCRUD extends Neo4jTest {

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
        bean.setName("Temporary"); // should be re-read from DB
        foobarKey = bean.getKey();
        tx = beginTx();
        try {
            Node node = beanStore.database().getNodeById(bean.getKey().getId());
            assertEquals(node.getProperty(NeoBeanStore.PROPERTY_TYPE), TestBean.class.getName(), "Type mismatch");
            assertEquals(node.getProperty(NeoBeanStore.PROPERTY_KEY), bean.getKey().getKey(), "Key mismatch");
            assertEquals(node.getProperty("name"), "FooBar", "Name mismatch");
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

    @Test
    public void testDelete() throws Exception {
        TestBean bean = new TestBean();
        bean.setName("Delete Me");
        bean.setKey(NodeKey.create());
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
            assertTrue(beanStore.delete(bean), "Bean not deleted");
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            try {
                beanStore.database().getNodeById(bean.getKey().getId());
                fail("Expected NotFoundException");
            }
            catch ( NotFoundException e ) {
                // expected
            }
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        TestBean bean = new TestBean();
        bean.setName("Delete me again");
        bean.setKey(NodeKey.create());
        Transaction tx = beginTx();
        try {
            assertFalse(beanStore.delete(bean), "Bean should not have been deleted");
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    @Test
    public void testDeleteNotFoundByKey() throws Exception {
        TestBean bean = new TestBean();
        bean.setName("And more deletion");
        bean.setKey(NodeKey.create());
        Transaction tx = beginTx();
        try {
            beanStore.store(bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        bean.setKey(NodeKey.arbitrary(bean.getKey().getId(), UUID.randomUUID().toString()));
        tx = beginTx();
        try {
            beanStore.database().getNodeById(bean.getKey().getId()); // check that the node exists
            assertFalse(beanStore.delete(bean), "Bean should not have been deleted");
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    @Test
    public void testNotFoundBeforeStore() throws Exception {
        TestBean bean = new TestBean();
        bean.setKey(NodeKey.create());
        bean.setName("Delete too early");
        Transaction tx = beginTx();
        try {
            assertFalse(beanStore.delete(bean), "Bean should not have been deleted");
            beanStore.store(bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            beanStore.database().getNodeById(bean.getKey().getId()); // should exist now
            assertTrue(beanStore.delete(bean), "Bean should have been deleted");
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

}
