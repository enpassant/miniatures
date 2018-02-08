import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.stream.Stream;

public class BufReadMulti {

    public static Reader createReader(char value) {
        return new Reader() {
            private int max = 30000;
            public int read(char[] buf, int offset, int length) {
                try {
                    Thread.sleep(1000);
                } catch(Exception e) {
                }

                for (int i=0; i<length-1; i++) {
                    max--;
                    if (max > 0) {
                        buf[offset + i] = '\n';
                    } else {
                        return -1;
                    }
                }
                buf[offset + length - 1] = value;
                return length;
            }

            public void close() {
            }
        };
    }

    public static void main(String[] args) {
        try {
            BufferedReader brf = new BufferedReader(new FileReader("/home/kalman/index.html"));
            BufferedReader br1 = new BufferedReader(createReader('1'));
            BufferedReader br2 = new BufferedReader(createReader('2'));

            Stream<String> lines = Stream.concat(br1.lines(), brf.lines());
            lines.forEach(l -> System.out.println(l));
        } catch(Exception e) {
        }
    }
}

