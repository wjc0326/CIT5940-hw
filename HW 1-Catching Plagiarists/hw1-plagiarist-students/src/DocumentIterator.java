import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DocumentIterator implements Iterator<String> {

    private Reader r;
    private int c = -1;
    private int n;
    

    public DocumentIterator(Reader r, int n) {
        this.r = r;
        this.n = n;
        try {
            // mark() at the beginning so that reset() won't throw error
            this.r.mark(300);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        skipNonLetters();
    }


    private void skipNonLetters() {
        try {
            this.c = this.r.read();
            while (!Character.isLetter(this.c) && this.c != -1) {
                this.c = this.r.read();
            }
        } catch (IOException e) {
            this.c = -1;    // no integer has been read
        }
    }


    @Override
    public boolean hasNext() {
        return (c != -1);
    }


    @Override
    public String next() {

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String answer = "";

        try {
            this.r.reset();

            // to avoid multiple non-letters
            skipNonLetters();

            for (int i = 0; i < this.n; i++) {
                // end the iteration when the length of the n-gram is less than n
                // (i.e. no next element during the loop)
                if (!hasNext()) {
                    return answer;
                }
                while (Character.isLetter(this.c)) {
                    answer = answer + (char) this.c;
                    this.c = this.r.read();
                }
                // only mark once(at the end of the first word, the first non-letter)
                if (i == 0) {
                    this.r.mark(300);
                }
                skipNonLetters();
            }

        } catch (IOException e) {
            throw new NoSuchElementException();
        }

        answer = answer.toLowerCase();
        return answer;

    }

//    public static void main(String[] args) {
//        Reader r = new StringReader("This      is        a        file.");
//        DocumentIterator iterator = new DocumentIterator(r, 2);
//        while (iterator.hasNext()) {
//            System.
//            out.println(iterator.next());
//        }
}

