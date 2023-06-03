import java.util.Comparator;

/**
 * @author ericfouh
 */
public class Similarities implements Comparable<Similarities> {
    /**
     * 
     */
    private String file1;
    private String file2;
    private int    count;


    /**
     * @param file1
     * @param file2
     */
    public Similarities(String file1, String file2) {
        this.file1 = file1;
        this.file2 = file2;
        this.setCount(0);
    }


    /**
     * @return the file1
     */
    public String getFile1() {
        return file1;
    }


    /**
     * @return the file2
     */
    public String getFile2() {
        return file2;
    }


    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }


    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public int compareTo(Similarities o) {
        //TODO
        if (this.getFile1().equals(o.getFile1()) && this.getFile2().equals(o.getFile2())) {
            return 0;
        } else if (this.getFile1().equals(o.getFile2()) && this.getFile2().equals(o.getFile1())) {
            return 0;
        } else if (this.getFile1().compareTo(o.getFile1()) == 0) {
            return this.getFile2().compareTo(o.getFile2());
        } else {
            return this.getFile1().compareTo(o.getFile1());
        }
    }

}

class SimilaritiesComparer implements Comparator<Similarities> {
    // descending order
    public int compare(Similarities s1, Similarities s2) {
        if (s1.getCount() == s2.getCount()) {
            if ((s1.getFile1()).compareTo(s2.getFile1()) == 0) {
                return -(s1.getFile2()).compareTo(s2.getFile2());
            }
            return -(s1.getFile1()).compareTo(s2.getFile1());
        } else if (s1.getCount() < s2.getCount()) {
            return 1;
        } else {
            return -1;
        }
    }
}
