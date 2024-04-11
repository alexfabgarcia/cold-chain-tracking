package br.ufscar.ppgcc.domain.event;

import br.ufscar.ppgcc.data.RawEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class EventService {

    private final RawEventRepository repository;
    private final ObjectMapper objectMapper;

    public EventService(RawEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void saveRawContentAsJson(final Object content) {
        var jsonContent = writeJsonSafely(content);
        repository.save(new RawEvent(jsonContent));
    }

    private String writeJsonSafely(final Object content) {
        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            return content.toString();
        }
    }

}
