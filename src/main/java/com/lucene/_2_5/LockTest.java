package com.lucene._2_5;

import junit.framework.TestCase;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import java.io.File;
import java.io.IOException;

/**
 * Created by xun.zhang on 2017/10/27.
 */
public class LockTest extends TestCase {

    private Directory dir;

    public void setUp() throws Exception {
        String indexDir = "E:\\work\\dev\\testlucene\\indexDir";
        dir = FSDirectory.open(new File(indexDir));
    }

    public void testWriteLock() throws IOException {
        IndexWriter writer1 = new IndexWriter(dir, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        IndexWriter writer2 = null;
        try {
            writer2 = new IndexWriter(dir, new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
        }catch(LockObtainFailedException e) {
            e.printStackTrace();
        }finally {
            writer1.close();
            assertNull(writer2);
        }
    }

}
