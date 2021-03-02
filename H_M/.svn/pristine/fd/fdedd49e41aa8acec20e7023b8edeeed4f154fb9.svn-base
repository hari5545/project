package models.utils;

import java.util.function.Consumer;

@FunctionalInterface
public interface HandlingConsumer<Target, ExObj extends Exception> {
    void accept(Target target) throws ExObj;
    
    static <Target> Consumer<Target> handlingConsumerBuilder(
            HandlingConsumer<Target, Exception> handlingConsumer) {     
    		return obj -> {
            try {
                handlingConsumer.accept(obj);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}