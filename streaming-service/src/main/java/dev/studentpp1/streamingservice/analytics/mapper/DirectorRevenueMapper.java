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

    // maps revenueBreakdownJson (String) to revenueBreakdown (Map) using jsonToMap method
    @Mapping(target = "revenueBreakdown", source = "revenueBreakdownJson", qualifiedByName = "jsonToMap")
    public abstract DirectorRevenueStatsDto toDto(DirectorRevenueStats stats);

    @Named("jsonToMap")
    protected Map<String, BigDecimal> jsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            // parse json as Map<String, BigDecimal>
            Map<String, BigDecimal> unsortedMap = objectMapper.readValue(
                    json,
                    new TypeReference<>() {} // save Map<String, BigDecimal> type in runtime
            );

            return unsortedMap.entrySet().stream()
                    .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1, // if we have two values with the same keys -> save only first (json hasn't duplicate keys)
                            LinkedHashMap::new  // save order of adding
                    ));

        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }
}