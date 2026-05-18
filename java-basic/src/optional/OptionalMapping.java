package optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class OptionalMapping {
    public static void main(String[] args) {
        List<Integer> nullOrInteger = new ArrayList<>(Arrays.asList(1, 2, 3, null, 5, null));

        for(int i=0;i<=20000;i++) {
            nullOrInteger.add(i);
        }

        List<Long> result = new ArrayList<>();
        int tryTest = 10;

        for(int i=0;i<tryTest;i++) {
            long start = System.currentTimeMillis();

            nullOrInteger.stream() // 1 -> 2 -> 3 -> null -> 5 -> null
                    .forEach(n -> System.out.println(n));

            long end = System.currentTimeMillis() - start;

            start = System.currentTimeMillis();

            for(Integer n : nullOrInteger) {
                System.out.println(n);
            }

            long end2 = System.currentTimeMillis() - start;

            result.add(end);
            result.add(end2);
        }

        for(int i=0;i<10;i++) {
            System.out.print(result.get(i*2) + " ");
        }
        System.out.println();
        for(int i=0;i<10;i++) {
            System.out.print(result.get(i*2+1) + " ");
        }

        // 결과
//        65 41 38 36 45 42 34 47 35 30
//        52 35 38 41 40 51 38 30 35 29
    }
}
