package ch.raffael.neobeans.impl;

import java.net.URL;

import org.testng.annotations.*;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;

import ch.raffael.neobeans.NodeKey;

import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestAnnotatedBeanMapping extends Neo4jTest {

    @Test
    public void testConfiguration() throws Exception {
        AnnotatedBean bean = new AnnotatedBean();
        beanStore.setMapping(AnnotatedBean.class, new AnnotatedBeanMappingFactory().createBeanMapping(beanStore.database(), bean));
        bean.setKey(NodeKey.create());
        bean.setName("Annotated Bean");
        bean.setUrl(new URL("http://www.example.com/"));
        bean.setIrrelevant(23);
        Transaction tx = beanStore.beginTx();
        try {
            beanStore.store(bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        NodeKey key = bean.getKey();
        tx = beanStore.database().beginTx();
        try {
            Node node = beanStore.database().getNodeById(key.getId());
            try {
                node.getProperty("irrelevant");
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
        bean = new AnnotatedBean();
        tx = beanStore.beginTx();
        try {
            beanStore.retrieve(key, bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        assertEquals(bean.getKey(), key, "Key mismatch");
        assertEquals(bean.getName(), "Annotated Bean", "Name mismatch");
        assertEquals(bean.getUrl(), new URL("http://www.example.com/"), "Url mismatch");
        assertEquals(bean.getIrrelevant(), 42, "Transient value mismatch");
    }

}
