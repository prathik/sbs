package rocks.thiscoder.sbs;

import org.testng.annotations.Test;

import java.io.File;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author prathik.raj
 */
@Test
public class BatchTest {
    @Test
    void constructBatchTest() throws SalesforceException {
        File file = mock(File.class);
        doReturn(true).when(file).isFile();

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        Batch batch = new Batch(file, salesforce);

    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Invalid file")
    void exceptionOnInvalidFileTest() throws SalesforceException {
        File file = new File("abcd.badfile");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        new Batch(file, salesforce);
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Uninitialized " +
            "salesforce object passed")
    void uninitializedSalesforceTest() throws SalesforceException {
        File file = mock(File.class);
        doReturn(true).when(file).isFile();

        Salesforce salesforce = mock(Salesforce.class);
        doReturn(null).when(salesforce).getSessionId();
        doReturn(null).when(salesforce).getInstance();

        new Batch(file, salesforce);
    }
}
