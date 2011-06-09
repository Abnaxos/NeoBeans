package ch.raffael.neobeans.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.testng.annotations.*;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;

import ch.raffael.neobeans.ConvertException;
import ch.raffael.neobeans.Converter;

import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestBeanPropertyMapping extends Neo4jTest {

    @Test
    public void testSimple() throws Exception {
        BeanPropertyMapping mapping = new BeanPropertyMapping(beanStore.database(), "name", null, null,
                                                              TestBean.class.getMethod("getName"),
                                                              TestBean.class.getMethod("setName", String.class));
        TestBean bean = new TestBean();
        bean.setName("Raffael");
        Node node;
        Transaction tx = beanStore.beginTx();
        try {
            node = beanStore.database().createNode();
            mapping.beanToEntity(node, bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            assertEquals(node.getProperty("name"), "Raffael", "Value in DB");
            node.setProperty("name", "Raffi");
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            mapping.entityToBean(node, bean);
            assertEquals(bean.getName(), "Raffi", "Bean value");
            bean.setName(null);
            mapping.beanToEntity(node, bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            try {
                node.getProperty("name");
                fail("NotFoundException expected");
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
    public void testConverter() throws Exception {
        BeanPropertyMapping mapping = new BeanPropertyMapping(beanStore.database(), "homepage",
                                                              new Converter() {
                                                                  @Override
                                                                  public Object fromNeo4j(Object value) {
                                                                      try {
                                                                          return new URL((String)value);
                                                                      }
                                                                      catch ( MalformedURLException e ) {
                                                                          throw new ConvertException(value, e);
                                                                      }
                                                                  }
                                                                  @Override
                                                                  public Object toNeo4j(Object value) {
                                                                      return value.toString();
                                                                  }
                                                              },
                                                              null,
                                                              TestBean.class.getMethod("getHomepage"),
                                                              TestBean.class.getMethod("setHomepage", URL.class));
        TestBean bean = new TestBean();
        Transaction tx = beginTx();
        Node node;
        try {
            bean.setHomepage(new URL("http://github.com/"));
            node = beanStore.database().createNode();
            mapping.beanToEntity(node, bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            assertEquals(node.getProperty("homepage"), "http://github.com/", "URL in DB");
            node.setProperty("homepage", "http://github.com/test");
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            mapping.entityToBean(node, bean);
            assertEquals(bean.getHomepage(), new URL("http://github.com/test"), "Bean value");
            bean.setHomepage(null);
            mapping.beanToEntity(node, bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            try {
                node.getProperty("homepage");
                fail("Expected not found exception");
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
    public void testSetFromNullToNull() throws Exception {
        BeanPropertyMapping mapping = new BeanPropertyMapping(beanStore.database(), "name", null, null,
                                                              TestBean.class.getMethod("getName"),
                                                              TestBean.class.getMethod("setName", String.class));
        TestBean bean = new TestBean();
        assertNull(bean.getName());
        Transaction tx = beginTx();
        Node node;
        try {
            node = beanStore.database().createNode();
            mapping.beanToEntity(node, bean);
            tx.success();
        }
        finally {
            tx.finish();
        }
        tx = beginTx();
        try {
            try {
                node.getProperty("name");
                fail("Expected not found exception");
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

    public static class TestBean {

        private String name;
        private URL homepage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public URL getHomepage() {
            return homepage;
        }

        public void setHomepage(URL homepage) {
            this.homepage = homepage;
        }
    }

}
