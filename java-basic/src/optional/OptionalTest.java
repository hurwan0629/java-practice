package optional;

import java.util.Optional;

public class OptionalTest {
    public static void main(String[] args) {
        // Optional<T>는 값이 있을 수도 있고, 없을 수도 있는 컨테이너 이다.
        // Optional은 제네릭 클래스로
        // Optional<T>와 같은 형태로 존재한다.

        // 예를 들어서 값이 있을수도 있고 없을 수 도 있게
        Optional<Integer> number1 = Optional.of(10);
        // private Optional(T value) { ... } 로 저장되어있어 기본 생성자는 사용하지 못한다.

        System.out.println(Optional.ofNullable(null)); // Optional.empty
        // private static final Optional<?> EMPTY = new Optional<>(null); 가 Option.empty이다.
        System.out.println(Optional.ofNullable(null).getClass()); // class java.util.Optional
//        System.out.println(new Optional<>("hello")); // private여서 사용 불가

    }

}
