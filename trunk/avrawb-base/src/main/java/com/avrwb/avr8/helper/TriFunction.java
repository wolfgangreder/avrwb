/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avrwb.avr8.helper;

/**
 *
 * @author wolfi
 * @param <S> first param
 * @param <T> second param
 * @param <U> third param
 * @param <R> return
 */
@FunctionalInterface
public interface TriFunction<S, T, U, R>
{

  R apply(S s,
          T t,
          U u);

}
