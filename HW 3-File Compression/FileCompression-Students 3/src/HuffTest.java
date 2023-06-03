import org.junit.Test;

import java.io.*;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class HuffTest {

    @Test
    public void makeHuffTreeAndShowCountsTest() {
        InputStream ins;
        {
            try {
                ins = new ByteArrayInputStream("abcabcbcc".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        Huff huff = new Huff();
        HuffTree resultTree;
        try {
            resultTree = huff.makeHuffTree(ins);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IHuffBaseNode root = resultTree.root();                 // 10
        IHuffBaseNode l = ((HuffInternalNode)root).left();      // 4 leaf 'c'
        IHuffBaseNode r = ((HuffInternalNode)root).right();     // 6
        IHuffBaseNode rl = ((HuffInternalNode)r).left();        // 3 leaf 'b'
        IHuffBaseNode rr = ((HuffInternalNode)r).right();       // 3
//        IHuffBaseNode ll = ((HuffInternalNode)l).left();        // NULL
//        IHuffBaseNode lr = ((HuffInternalNode)l).right();       // NULL
        IHuffBaseNode rrl = ((HuffInternalNode)rr).left();      // 1 leaf 'EOF'
        IHuffBaseNode rrr = ((HuffInternalNode)rr).right();     // 2 leaf 'a'

        assertFalse(root.isLeaf());
        assertEquals(10, root.weight());

        assertTrue(l.isLeaf());
        assertEquals(4, l.weight());
        assertEquals('c', ((HuffLeafNode)l).element());

        assertFalse(r.isLeaf());
        assertEquals(6, r.weight());

        assertTrue(rl.isLeaf());
        assertEquals(3, rl.weight());
        assertEquals('b', ((HuffLeafNode)rl).element());

        assertFalse(rr.isLeaf());
        assertEquals(3, rr.weight());

        assertTrue(rrl.isLeaf());
        assertEquals(1, rrl.weight());
        assertEquals((1 << 8), ((HuffLeafNode)rrl).element());      // EOF

        assertTrue(rrr.isLeaf());
        assertEquals(2, rrr.weight());
        assertEquals('a', ((HuffLeafNode)rrr).element());

        assertEquals(3, huff.showCounts().size());      // {97=2, 98=3, 99=4}
        assertTrue(huff.showCounts().containsKey(97));
    }

    @Test
    public void makeTableAndGetCodeTest() {
        InputStream ins;
        {
            try {
                ins = new ByteArrayInputStream("abcabcbcc".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        Huff huff = new Huff();
        HuffTree resultTree;
        try {
            resultTree = huff.makeHuffTree(ins);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<Integer, String> encodeMap = huff.makeTable();
        assertTrue(encodeMap.containsKey(97));      // 'a'
        assertEquals("111", huff.getCode(97));
        assertTrue(encodeMap.containsKey(98));      // 'b'
        assertEquals("10", huff.getCode(98));
        assertTrue(encodeMap.containsKey(99));      // 'c'
        assertEquals("0", huff.getCode(99));
        assertTrue(encodeMap.containsKey(1 << 8));  // 'EOF'
        assertEquals("110", huff.getCode(1 << 8));
    }

    @Test
    public void writeHeaderAndHeaderSizeTest() throws IOException {
        InputStream ins;
        {
            try {
                ins = new ByteArrayInputStream("abcabcbcc".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        Huff huff = new Huff();
        HuffTree resultTree = huff.makeHuffTree(ins);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int length = huff.writeHeader(new BitOutputStream(out));
        assertEquals(75, length);
        out.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        BitInputStream inbit = new BitInputStream(in);
        int magic = inbit.read(32);         // test for magic number
        assertEquals(1234567873, magic);

        assertEquals(75, huff.headerSize());
    }

    @Test
    public void writeTest() {
        Huff huff = new Huff();
        int outputLength = huff.write("simple_inFile.txt", "simple_outFile.txt", true);
        assertEquals(96, outputLength);
//        BitInputStream inbitFile = new BitInputStream(new FileInputStream("simple_outFile.txt"));
//        int readinBit;
//        while ((readinBit = inbitFile.read(1)) != -1) {
//            System
//                    .out.println(readinBit);
//        }
    }

    @Test
    public void readHeaderTest() throws IOException {
        Huff huff = new Huff();
        huff.write("simple_inFile.txt", "simple_outFile.txt", true);
        BitInputStream in = new BitInputStream(new FileInputStream("simple_outFile.txt"));
        HuffTree rebuildTree = huff.readHeader(in);

        IHuffBaseNode root = rebuildTree.root();
        IHuffBaseNode l = ((HuffInternalNode)root).left();      // leaf 'c'
        IHuffBaseNode r = ((HuffInternalNode)root).right();
        IHuffBaseNode rl = ((HuffInternalNode)r).left();        // leaf 'b'
        IHuffBaseNode rr = ((HuffInternalNode)r).right();
        IHuffBaseNode rrl = ((HuffInternalNode)rr).left();      // leaf 'EOF'
        IHuffBaseNode rrr = ((HuffInternalNode)rr).right();     // leaf 'a'

        assertFalse(root.isLeaf());
        assertEquals(0, root.weight());

        assertTrue(l.isLeaf());
        assertEquals(0, l.weight());
        assertEquals('c', ((HuffLeafNode)l).element());

        assertFalse(r.isLeaf());
        assertEquals(0, r.weight());

        assertTrue(rl.isLeaf());
        assertEquals(0, rl.weight());
        assertEquals('b', ((HuffLeafNode)rl).element());

        assertFalse(rr.isLeaf());
        assertEquals(0, rr.weight());

        assertTrue(rrl.isLeaf());
        assertEquals(0, rrl.weight());
        assertEquals((1 << 8), ((HuffLeafNode)rrl).element());      // EOF

        assertTrue(rrr.isLeaf());
        assertEquals(0, rrr.weight());
        assertEquals('a', ((HuffLeafNode)rrr).element());
    }

    @Test
    public void uncompressTest() {
        Huff huff = new Huff();
        int fileSize = huff.uncompress("simple_outFile.txt", "simple_reFile.txt");
        assertEquals(72, fileSize);
    }

}