/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

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

    protected HashMap<String, String[]> parseSearchInfo(String infoType, String infoArrStr) {
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
