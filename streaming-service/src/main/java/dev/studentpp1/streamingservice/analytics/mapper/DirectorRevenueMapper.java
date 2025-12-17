package dev.studentpp1.streamingservice.analytics.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStats;
import dev.studentpp1.streamingservice.analytics.dto.DirectorRevenueStatsDto;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DirectorRevenueMapper {

    protected ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Mapping(target = "revenueBreakdown", source = "revenueBreakdownJson", qualifiedByName = "jsonToMap")
    public abstract DirectorRevenueStatsDto toDto(DirectorRevenueStats stats);

    @Named("jsonToMap")
    protected Map<String, BigDecimal> jsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            Map<String, BigDecimal> unsortedMap = objectMapper.readValue(
                json,
                new TypeReference<>() {
                }
            );

            return unsortedMap.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));

        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}