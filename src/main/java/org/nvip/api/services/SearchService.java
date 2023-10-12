package org.nvip.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {

    private HashMap<String, String[]> parseSearchInfo(String infoType, String infoArrStr) {
        HashMap<String, String[]> infoMap = new HashMap<>();

        if (Objects.equals(infoType, "cvssScores")) {
            infoMap.put(infoType, infoArrStr.split(";"));
        } else if (Objects.equals(infoType, "vdoNounGroups")) {
            String[] vdoEntities = infoArrStr.split("\\|");
            for (String vdoEntity : vdoEntities) {
                String[] vdo = vdoEntity.split(":");
                infoMap.put(vdo[0], vdo[1].split(";"));
            }
        }
        return infoMap;
    }

    public Map<String, Map<String, String[]>> getSearchInfo() {
        return Stream
                .of(new String[][] { { "cvssScores", "CRITICAL;HIGH;LOW;MEDIUM" },
                        { "vdoNounGroups", "Impact Method:Man-in-the-Middle;Authentication Bypass;Code Execution;Context Escape;Trust Failure|Context:Channel;Physical Hardware;Application;Host OS;Firmware;Guest OS;Hypervisor|Mitigation:Physical Security;MultiFactor Authentication;HPKP/HSTS;ASLR;Sandboxed|Attack Theater:Limited Rmt;Local;Remote;Physical|Logical Impact:Read;Resource Removal;Write;Indirect Disclosure;Service Interrupt;Privilege Escalation" } })
                .collect(Collectors.toMap(data -> data[0], data -> parseSearchInfo(data[0], data[1])));
    }
}
