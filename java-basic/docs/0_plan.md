# Java Basic 학습 계획표

## 목표

자바 기초 문법을 넘어 실무 코드에서 자주 쓰이는 핵심 개념을 작은 예제와 실습으로 익힌다.
각 주제는 `src` 아래에 직접 실행 가능한 예제를 만들고, 개념 정리와 회고를 함께 남기는 방식으로 진행한다.

## 진행 원칙

- 한 번에 하나의 주제를 학습한다.
- 각 주제마다 최소 1개 이상의 실행 예제를 작성한다.
- 예제는 `main` 메서드로 바로 실행할 수 있게 만든다.
- 개념보다 동작을 먼저 확인하고, 이후 코드에 사용 이유를 정리한다.
- 예외 상황이나 실패 케이스를 함께 실습한다.

## 계획표

| 순서 | 주제 | 학습 내용 | 실습 산출물 | 완료 기준 |
| --- | --- | --- | --- | --- |
| 1 | 프로젝트 기본 구조 | `src`, `main`, 클래스 실행 흐름 | `Main.java` 실행 예제 | 콘솔 출력 프로그램을 직접 실행할 수 있다 |
| 2 | Optional | `null` 처리, `ofNullable`, `orElse`, `orElseGet`, `map`, `flatMap` | Optional 사용 예제 | `null` 체크를 Optional로 대체할 수 있다 |
| 3 | 함수형 프로그래밍 | 람다식, 메서드 참조, `Function`, `Predicate`, `Consumer`, `Supplier` | 함수형 인터페이스 예제 | 람다식을 직접 작성하고 표준 함수형 인터페이스를 사용할 수 있다 |
| 4 | Stream 기초 | `filter`, `map`, `sorted`, `collect`, `forEach` | 컬렉션 데이터 처리 예제 | 반복문 기반 코드를 Stream으로 바꿀 수 있다 |
| 5 | Exception 및 오류 | checked/unchecked exception, `try-catch-finally`, `throw`, `throws` | 예외 처리 예제 | 예외를 잡아야 하는 경우와 던져야 하는 경우를 구분할 수 있다 |
| 6 | 인터페이스 | 인터페이스 정의, 구현체, 다형성, default/static 메서드 | 결제/알림 같은 역할 분리 예제 | 구현체를 바꿔도 호출 코드가 유지되는 구조를 만들 수 있다 |
| 7 | 추상 클래스 | 추상 메서드, 공통 상태와 공통 로직, 상속 구조 | 템플릿 메서드 예제 | 인터페이스와 추상 클래스의 차이를 설명할 수 있다 |
| 8 | 익명 함수와 익명 클래스 | 익명 클래스, 람다로의 전환, 콜백 구조 | 정렬/이벤트 처리 예제 | 익명 클래스와 람다의 관계를 이해하고 변환할 수 있다 |
| 9 | 비동기 기초 | `Thread`, `Runnable`, `ExecutorService`, `Future` | 간단한 비동기 작업 실행 예제 | 메인 흐름과 별도 작업 흐름을 구분할 수 있다 |
| 10 | CompletableFuture | `supplyAsync`, `thenApply`, `thenAccept`, `exceptionally`, 조합 | 비동기 파이프라인 예제 | 비동기 결과를 연결하고 예외를 처리할 수 있다 |
| 11 | 종합 실습 | Optional, Stream, Exception, 인터페이스, 비동기를 함께 사용 | 작은 콘솔 애플리케이션 | 여러 개념을 한 흐름의 프로그램 안에서 사용할 수 있다 |

## 추천 디렉터리 구조

```text
src/
  Main.java
  optional/
  functional/
  stream/
  exception/
  abstraction/
  async/
```

## 주제별 체크리스트

### Optional

- [ ] `Optional.of`, `Optional.ofNullable`, `Optional.empty` 차이 실습
- [ ] `orElse`와 `orElseGet` 차이 실습
- [ ] `map`과 `flatMap` 차이 실습
- [ ] Optional을 필드나 파라미터로 남용하지 않는 이유 정리

### 함수형 프로그래밍

- [ ] 람다식 문법 실습
- [ ] 메서드 참조 실습
- [ ] 표준 함수형 인터페이스 사용
- [ ] 직접 함수형 인터페이스 정의

### Exception 및 오류

- [ ] checked exception과 unchecked exception 비교
- [ ] 예외를 처리하는 코드와 다시 던지는 코드 작성
- [ ] 사용자 정의 예외 작성
- [ ] `finally` 또는 try-with-resources 실습

### 인터페이스/추상클래스

- [ ] 인터페이스로 역할 정의
- [ ] 여러 구현체 작성
- [ ] 추상 클래스로 공통 로직 분리
- [ ] 인터페이스와 추상 클래스 선택 기준 정리

### 익명함수

- [ ] 익명 클래스 작성
- [ ] 익명 클래스를 람다식으로 변환
- [ ] 정렬 기준을 람다로 전달

### 비동기

- [ ] `Thread`와 `Runnable`로 작업 실행
- [ ] `ExecutorService`로 작업 제출
- [ ] `Future`로 결과 받기
- [ ] `CompletableFuture`로 비동기 작업 연결
- [ ] 비동기 예외 처리

## 기록 방식

각 주제를 마칠 때 아래 항목을 간단히 기록한다.

```text
주제:
작성한 예제:
알게 된 점:
헷갈린 점:
다음에 다시 볼 내용:
```

