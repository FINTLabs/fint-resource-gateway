package no.fintlabs.fint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Objects;

@Slf4j
public class FintListendableFutureCallback<T> implements ListenableFutureCallback<SendResult<String, EntityMessage<T>>> {


    @Override
    public void onFailure(Throwable ex) {
        log.info("Unable to send message due to : " + ex.getMessage());
    }

    @Override
    public void onSuccess(SendResult<String, EntityMessage<T>> result) {
        log.info("Sent message=[" + Objects.requireNonNull(result).getProducerRecord().value() +
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
    }
}
