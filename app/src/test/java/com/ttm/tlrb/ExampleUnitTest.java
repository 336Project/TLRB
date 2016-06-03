package com.ttm.tlrb;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                for (int i = 0;i<4;i++){
                    subscriber.onNext(""+i/0);
                    System.out.println("in");
                }
                subscriber.onCompleted();
            }
        }).flatMap(new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String s) {
                return Observable.just(Integer.valueOf(s));
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Throwable--"+ e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(integer);
            }
        });
    }

}