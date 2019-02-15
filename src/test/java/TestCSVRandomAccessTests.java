/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.LoadListener;
import org.rappsilber.utils.UpdateableInteger;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public class TestCSVRandomAccessTests {
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();
    
    public TestCSVRandomAccessTests() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testAssyncNext() {
        int testsize = 10000;
        try {
            // create largish a csv
            File createdFile= folder.newFile("largefile.csv");
            PrintWriter pw = new PrintWriter(createdFile);
            for (int i = 0; i< testsize;i++) {
                pw.println(i+","+(1+i)+","+(2+i)+","+(3+i)+"," + (4+i) + "," +
                        (5+i)+"," + (6+i) + "," + (7 + i) + "," + (8+i) +"," +
                        (9+i)+ "," + (10+i));
            }
            pw.close();
            CSVRandomAccess ra = new CSVRandomAccess(',', '"');
            final AtomicInteger count = new AtomicInteger(0);
            LoadListener progressListener = new LoadListener() {
                @Override
                public void listen(int row, int column) {
                    System.out.println("Lines processed: " + count.get() + "\nlines read : " +row);
                }
            };
            ra.addListenerProgress(progressListener);
            ra.openFileAsync(createdFile, false);
            while (ra.next()) {
                assertEquals(count.get(), ra.getRow());
                for (int i = 0; i<10; i++) {
                    assertEquals(count.get(), (int) ra.getInteger(i) - i);
                }
                count.incrementAndGet();
            }
            assertEquals(testsize, count.get());
        } catch (IOException ex) {
            Logger.getLogger(TestCSVRandomAccessTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
