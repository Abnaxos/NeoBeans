package ch.raffael.neobeans.impl;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.*;

import com.google.common.io.Files;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Neo4jTest {

    private static File dbDirectory;
    protected static DefaultNeoBeanStore beanStore;

    @BeforeSuite
    public void createTestDatabase() {
        dbDirectory = Files.createTempDir();
        System.out.println("Creating test database at: " + dbDirectory);
        beanStore = new DefaultNeoBeanStore(new EmbeddedGraphDatabase(dbDirectory.toString()));
    }

    @AfterSuite(alwaysRun = true)
    public void deleteTestDatabase() {
        if ( beanStore != null ) {
            try {
                beanStore.shutdown();
            }
            finally {
                beanStore = null;
            }
        }
        if ( dbDirectory != null ) {
            try {
                Files.deleteRecursively(dbDirectory);
            }
            catch ( IOException e ) {
                System.err.print("Error deleting test database: ");
                e.printStackTrace(System.err);
            }
            finally {
                dbDirectory = null;
            }
        }
    }

    protected Transaction beginTx() {
        return beanStore.beginTx();
    }

}
