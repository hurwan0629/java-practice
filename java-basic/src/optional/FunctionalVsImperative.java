package optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionalVsImperative {
    public static void main(String[] args) {
        List<Integer> nullOrInteger = new ArrayList<>(Arrays.asList(1, 2, 3, null, 5, null));

        for(int i=0;i<=20000;i++) {
            nullOrInteger.add(i);
        }

        List<Long> result = new ArrayList<>();
        int tryTest = 10;

        for(int i=0;i<tryTest;i++) {
            long start = System.nanoTime();

            long sum1 = nullOrInteger.stream()
                    .filter(n -> n != null)
                    .mapToLong(n -> n)
                    .sum();

            long end = System.nanoTime() - start;

            start = System.nanoTime();

            long sum2 = 0;
            for (Integer n : nullOrInteger) {
                if (n != null) {
                    sum2 += n;
                }
            }

            long end2 = System.nanoTime() - start;

            System.out.println("stream: " + end);
            System.out.println("for: " + end2);
            System.out.println(sum1 + ", " + sum2);

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
//        7918100 1835500 536200 428200 131500 162700 107600 101300 150100 105200
//        1621300 1160300 1079400 1059500 231900 160700 171300 156800 178100 160100
        // 결론: 둘은 그냥 용도 차이 정도이다.
    }
}
