package no.fintlabs.fint;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityMessage<T> {

    private T data;
}
