package com.lucene._2_3;

import junit.framework.TestCase;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

/**
 * Created by xun.zhang on 2017/10/27.
 */
public class IndexingTest extends TestCase {

    protected String[] ids = {"1","2"};
    protected String[] unindexed = {"Netherlands", "Italy"};
    protected String[] unstored = {"Amsterdam has lots of bridges", "Venice has lots of canals"};
    protected String[] text = {"Amsterdam","Venice"};

    private Directory directory;

    public void setUp() throws Exception {
        directory = new RAMDirectory();
        IndexWriter writer = getWriter();
        for (int i = 0; i < ids.length; i++) {
            Document doc = new Document();
            doc.add(new Field("id", ids[i] , Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field("country", unindexed[i], Field.Store.YES, Field.Index.NO));
            doc.add(new Field("contents", unindexed[i], Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field("city",text[i], Field.Store.YES, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        writer.close();
    }

    private IndexWriter getWriter() throws IOException {
        return new IndexWriter(directory, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
    }

    protected int getHitCount(String fieldName, String searchString) throws IOException {
        IndexSearcher searcher = new IndexSearcher(directory);
        Term t = new Term(fieldName, searchString);
        Query query = new TermQuery(t);
        TopDocs topDocs = searcher.search(query, 1);
        int hitCount = topDocs.totalHits;
        searcher.close();
        return hitCount;
    }

    public void testUpdate() throws IOException {
        assertEquals(1, getHitCount("city", "Amsterdam"));
        IndexWriter writer = getWriter();
        Document doc = new Document();
        doc.add(new Field("id","1",Field.Store.YES,Field.Index.NOT_ANALYZED));
        doc.add(new Field("country","Netherlands",Field.Store.YES,Field.Index.NO));
        doc.add(new Field("contents","Den Haag has a lot of museums",Field.Store.NO,Field.Index.ANALYZED));
        doc.add(new Field("city","DenHaag",Field.Store.YES,Field.Index.ANALYZED));
        writer.updateDocument(new Term("id","1"),doc);
        writer.optimize();
        writer.close();
        assertEquals(0,getHitCount("city","Amsterdam"));
        assertEquals(1,getHitCount("city","DenHaag"));
    }
}
