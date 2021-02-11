import java.util.stream.IntStream;

public class JavacTest {

    public static void main(String[] args) {
        IntStream.iterate(1, v -> v + 1)
                .takeWhile(i -> i < 10)
                .mapToObj(i -> new int[] { i })
                .forEach(array -> {
                    for (int i : array) {
                        System.out.println(i);
                    }
                });
    }
}
