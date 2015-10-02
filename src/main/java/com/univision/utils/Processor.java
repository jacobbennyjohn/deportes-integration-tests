package com.univision.utils;

import org.springframework.batch.item.ItemProcessor;

/**
 */
public class Processor implements ItemProcessor {
    @Override
    public Object process(Object o) throws Exception {
        System.out.println("processing " + o);
        return null;
    }
}
