import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CharCounter implements ICharCounter, IHuffConstants {

    private Map<Integer, Integer> counterMap = new HashMap<>();

    @Override
    public int getCount(int ch) {
        // @throws the appropriate exception if ch isn't a valid chunk/character
        if (ch < 0 || ch >= PSEUDO_EOF) {
            throw new IllegalArgumentException("Invalid char");
        }
        if (!counterMap.containsKey(ch)) {
            return 0;
        } else {
            return counterMap.get(ch);
        }
    }

    @Override
    public int countAll(InputStream stream) throws IOException {
        BitInputStream bits = new BitInputStream(stream);
        int inbits;
        // read each character and add it to the hashmap
        while ((inbits = bits.read(BITS_PER_WORD)) != -1) {
//            System
//            .out.println("char: " + (char)inbits);
            this.add(inbits);
        }
        // iterate over the hashmap and add each value to the total count
        int totalCount = 0;
        for (Map.Entry<Integer,Integer> entry : counterMap.entrySet()) {
            totalCount += entry.getValue();
        }
        return totalCount;
    }

    @Override
    public void add(int i) {
        if (!counterMap.containsKey(i)) {
            counterMap.put(i, 1);
        } else {
            counterMap.put(i, counterMap.get(i) + 1);
        }
    }

    @Override
    public void set(int i, int value) {
        counterMap.put(i, value);
    }

    @Override
    public void clear() {
        counterMap.clear();
    }

    @Override
    public Map<Integer, Integer> getTable() {
        return counterMap;
    }


//    public static void main(String[] args) {
//        ICharCounter cc = new CharCounter();
//        InputStream ins;
//        {
//            try {
//                ins = new ByteArrayInputStream("charcountertest".getBytes("UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        int actualSize;
//        {
//            try {
//                actualSize = cc.countAll(ins);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        System
//        .out.println("Map: " + cc.getTable());
//        System
//        .out.println("char " + (char)117 + " has count of: " + cc.getCount(117));
//    }
}
