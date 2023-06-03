import java.io.*;
import java.util.*;


public class Huff implements ITreeMaker,IHuffEncoder,IHuffConstants,IHuffModel,IHuffHeader {
    private Map<Integer, Integer> counterMap = new HashMap<>();
    private Map<Integer, String> encodeMap = new HashMap<>();
    private HuffTree huffTree;
    private int headerSize;
    private int virtualHeaderSize;


    // ******* ITreeMaker *******
    @Override
    // DONE
    public HuffTree makeHuffTree(InputStream stream) throws IOException {
        // STEP 1: build the frequency table using CharCounter
        ICharCounter cc = new CharCounter();
        cc.countAll(stream);
        counterMap = cc.getTable();

        // STEP 2: make a priority queue of nodes
        Queue<HuffTree> nodesQueue = new PriorityQueue();
        for (Map.Entry<Integer,Integer> entry : counterMap.entrySet()) {
            HuffTree currNode = new HuffTree(entry.getKey(), entry.getValue());
            nodesQueue.add(currNode);
        }
        HuffTree eofNode = new HuffTree(PSEUDO_EOF, 1);
        nodesQueue.add(eofNode);

        // STEP 3: make parent nodes up to the root
        while (nodesQueue.size() > 1) {
            // Dequeue 2 lowest-priority nodes
            HuffTree firstNode = nodesQueue.remove();
            HuffTree secondNode = nodesQueue.remove();

            // Make a parent for the two nodes
            HuffTree insertNode = new HuffTree(firstNode.root(), secondNode.root(),
                    (firstNode.weight() + secondNode.weight()));

            // Enqueue parent back into priority queue
            nodesQueue.add(insertNode);

        }
        this.huffTree = nodesQueue.remove();
        return this.huffTree;
    }


    // ******* IHuffEncoder *******
    // DONE
    private void huffmanGetCodes(IHuffBaseNode node, String prefix, Map<Integer, String> output) {
        if (node.isLeaf()) {
            output.put(((HuffLeafNode)node).element(), prefix);
        } else {
            huffmanGetCodes(((HuffInternalNode)node).left(), prefix + "0", output);
            huffmanGetCodes(((HuffInternalNode)node).right(), prefix + "1", output);
        }
    }

    @Override
    // DONE
    public Map<Integer, String> makeTable() {
        huffmanGetCodes(this.huffTree.root(), "", encodeMap);
        return this.encodeMap;
    }

    @Override
    // DONE
    public String getCode(int i) {
        return this.encodeMap.get(i);
    }

    @Override
    // DONE
    public Map<Integer, Integer> showCounts() {
        return this.counterMap;
    }


    // ******* IHuffHeader *******
    @Override
    // DONE
    public int headerSize() {
        return this.headerSize;
    }

    // DONE
    private void writeTreeHelper(BitOutputStream out, IHuffBaseNode node) {
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            out.write(1, 1);
            out.write(BITS_PER_WORD + 1, ((HuffLeafNode)node).element());
            this.headerSize += (BITS_PER_WORD + 2);
        } else {
            out.write(1, 0);
            this.headerSize += 1;
            writeTreeHelper(out, ((HuffInternalNode)node).left());
            writeTreeHelper(out, ((HuffInternalNode)node).right());
        }
    }

    @Override
    // DONE
    public int writeHeader(BitOutputStream out) {
        // STEP 1: write out the magic number
        this.headerSize = 0;
        out.write(BITS_PER_INT, MAGIC_NUMBER);
        this.headerSize += BITS_PER_INT;

        // STEP 2: write the tree using pre-order traversal
        writeTreeHelper(out, huffTree.root());

        return this.headerSize;
    }

    // DONE
    private void virtualWriteTreeHelper(IHuffBaseNode node) {
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            this.virtualHeaderSize += (BITS_PER_WORD + 2);
        } else {
            this.virtualHeaderSize += 1;
            virtualWriteTreeHelper(((HuffInternalNode)node).left());
            virtualWriteTreeHelper(((HuffInternalNode)node).right());
        }
    }

    /**
     * Used to calculate the size of the header, only used to calculate the whole size of
     * the compressed file and check whether the compressed file is truly "compressed" compared
     * to the original file size
     *
     * @return the size of the header
     */
    // DONE
    private int virtualWriteHeader() {
        // STEP 1: write out the magic number
        this.virtualHeaderSize = 0;
        this.virtualHeaderSize += BITS_PER_INT;

        // STEP 2: write the tree using pre-order traversal
        virtualWriteTreeHelper(huffTree.root());

        return this.virtualHeaderSize;
    }

    // DONE
    private HuffTree rebuildHuffTreeHelper(BitInputStream in) throws IOException {
        int inbit = in.read(1);
        if ((inbit & 1) == 1) {           // leaf node
            int leaf = in.read(9);
            HuffTree leafNode = new HuffTree(leaf, 0);
            return leafNode;
        } else {        // internal node
            HuffTree leftChild = rebuildHuffTreeHelper(in);
            HuffTree rightChild = rebuildHuffTreeHelper(in);
            HuffTree internalNode = new HuffTree(leftChild.root(), rightChild.root(), 0);
            return internalNode;
        }
    }

    @Override
    // DONE
    public HuffTree readHeader(BitInputStream in) throws IOException {
        // STEP 1: check the first 32 bits to see whether it is the magic number
        if (in.read(BITS_PER_INT) != MAGIC_NUMBER) {
            throw new IOException();
        }

        // STEP 2: use the remaining bits in header to rebuild the huffman tree
        return rebuildHuffTreeHelper(in);
    }


    // ******* IHuffModel *******
    @Override
    // DONE
    public int write(String inFile, String outFile, boolean force) {
        // STEP 1: calculate the size of the compressed file
        //         and check whether it's truly "compressed"
        BitInputStream bitin;
        BitOutputStream bitout;
        int fileSize = 0;               // the size of the compressed file
        int uncompressedFileSize = 0;   // the size of the uncompressed file

        try {
            bitin = new BitInputStream(new FileInputStream(inFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // 1. get the header length
        Huff huff = new Huff();
        HuffTree resultTree;
        try {
            resultTree = huff.makeHuffTree(bitin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        counterMap = huff.showCounts();
        encodeMap = huff.makeTable();
        int virtualHeaderLength = huff.virtualWriteHeader();
        fileSize += virtualHeaderLength;

        // 2. get the content length (calculate using counterMap + encodeMap)
        for (Map.Entry<Integer, String> entry : encodeMap.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            if (counterMap.containsKey(key)) {      // exclude PSEUDO-EOF
                fileSize += (value.length() * counterMap.get(key));
                uncompressedFileSize += counterMap.get(key);
            } else {        // PSEUDO-EOF (only 1 EOF in the compressed file)
                fileSize += value.length();
            }
        }

        // 3. calculate the length of the compressed file
        // (add extra bits after pseudo-EOF to make the total number of bits a multiple of eight)
        fileSize = (int)Math.ceil((float)fileSize / BITS_PER_WORD) * BITS_PER_WORD;

        // 4. calculate the length of the original file (calculate using counterMap)
        uncompressedFileSize *= BITS_PER_WORD;

        if (force || fileSize < uncompressedFileSize) {
            try {
                bitout = new BitOutputStream(new FileOutputStream(outFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            // STEP 2: write the header
            int headerLength = huff.writeHeader(bitout);
            int contentLength = 0;
            // need to reread the input file,
            // because we have iterated the input file once in makeHuffTree()
            try {
                bitin = new BitInputStream(new FileInputStream(inFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            int inchar;
            // STEP 3: write the bits needed to encode each character of the input file
            while (true) {
                try {
                    if ((inchar = bitin.read(BITS_PER_WORD)) == -1) {
                        break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String content = encodeMap.get(inchar);
                // write the content to BitOutputStream bit by bit
                for (int i = 0; i < content.length(); i++) {
                    bitout.write(1, content.charAt(i));
                }
                contentLength += content.length();
            }

            // STEP 4: write the PSEUDO-EOF and extra bits to reach multiple of 8
            for (int j = 0; j < encodeMap.get(PSEUDO_EOF).length(); j++) {
                bitout.write(1, encodeMap.get(PSEUDO_EOF).charAt(j));
                contentLength += 1;
            }

            int totalLength = headerLength + contentLength;
            int extraBit = fileSize - totalLength;
            for (int k = 0; k < extraBit; k++) {
                bitout.write(1, 0);
            }
            bitout.close();
        }
        bitin.close();
        return fileSize;
    }

    @Override
    public int uncompress(String inFile, String outFile) {
        BitInputStream bitin;
        BitOutputStream bitout;
        HuffTree rebuildTree;
        int fileSize = 0;
        int bits;

        try {
            bitin = new BitInputStream(new FileInputStream(inFile));
            bitout = new BitOutputStream(new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            rebuildTree = readHeader(bitin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IHuffBaseNode root = rebuildTree.root();

        while (true) {
            try {
                if ((bits = bitin.read(1)) == -1) {
                    bitout.close();
                    System.err.println("should not happen! trouble reading bits");
                } else {
                    if ((bits & 1) == 0) {      // read a 0, go left in tree
                        root = ((HuffInternalNode)root).left();
                    } else {            // read a 1, go right in tree
                        root = ((HuffInternalNode)root).right();
                    }

                    if (root.isLeaf()) {             // at leaf node in tree
                        if (((HuffLeafNode)root).element() == PSEUDO_EOF) {   // leaf node is EOF
                            bitout.close();
                            break;
                        } else {        // write char stored in leaf node
                            int charToWrite = ((HuffLeafNode)root).element();
                            bitout.write(BITS_PER_WORD, charToWrite);
                            fileSize += BITS_PER_WORD;
                            root = rebuildTree.root();
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        bitin.close();
        bitout.close();
        return fileSize;
    }


//    public static void main(String[] args) throws IOException {
//        ICharCounter cc = new CharCounter();
//        InputStream ins;
//        {
//            try {
//                ins = new ByteArrayInputStream("abcabcbcc".getBytes("UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        Huff huff = new Huff();
//        HuffTree resultTree;
//        try {
//            resultTree = huff.makeHuffTree(ins);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Map<Integer, Integer> counterMap = huff.showCounts();
////        System
////                .out.println("Map: " + counterMap);
//
////        System
////        .out.println(resultTree);
//        Map<Integer, String> encodeMap = huff.makeTable();
////        System
////        .out.println(encodeMap);
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        BitOutputStream outbit = new BitOutputStream(out);
//
//        int virtualHeaderSize = huff.virtualWriteHeader();
//        System
//                .out.println("virtual length: " + virtualHeaderSize);
//
//        int length = huff.writeHeader(outbit);
//        System
//                .out.println("length: " + length);
////        System
////                .out.println("output: " + out);
//        outbit.write(5, 0);
//        outbit.close();
//        out.close();
//
////        System
////                .out.println("size (num of bytes) of out stream: " + out.size());
////        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
////        BitInputStream inbit = new BitInputStream(in);
////        int magic;
////        magic = inbit.read(BITS_PER_INT);
////        System
////                .out.println("magic number: " + magic);
////
////        int tillC;
////        tillC = inbit.read(11);
////        System
////                .out.println("binary till c's ASCII: " + tillC);      // 611
////
////        int tillB;
////        tillB = inbit.read(11);
////        System
////                .out.println("binary till b's ASCII: " + tillB);      // 610
////
////        int tillEOF;
////        tillEOF = inbit.read(11);
////        System
////                .out.println("binary till EOF's ASCII: " + tillEOF);      // 768
////
////        int resA;
////        while ((resA = inbit.read(1)) != -1) {
////            System
////                    .out.println(resA);
////        }
////        System
// .out.println((float) 75 / 8);
////        System
// .out.println(Math.ceil((float)75 / 8));
////        System
// .out.println((int)Math.ceil((float)75 / 8) * 8);

//        int outputLength = huff.write("large_inFile.txt", "large_outFile.txt", true);
//        System
//        .out.println("out put file's length: " + outputLength);
//        BitInputStream inbitFile = new BitInputStream(new FileInputStream("simple_outFile.txt"));
//        int readinBit;
//        while ((readinBit = inbitFile.read(1)) != -1) {
//            System
//                    .out.println(readinBit);
//        }

//        BitInputStream in = new BitInputStream(new FileInputStream("simple_outFile.txt"));
//        HuffTree rebuildTree = huff.readHeader(in);
//        System
//        .out.println(rebuildTree);
//        BitOutputStream out = new BitOutputStream(new FileOutputStream("simple_reFile.txt"));
//        int fileSize = huff.uncompress("large_outFile.txt", "large_reFile.txt");
//        System
//        .out.println("fileSize: " + fileSize);
//    }


}
