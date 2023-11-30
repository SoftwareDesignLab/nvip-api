package org.nvip.api.services;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SearchServiceTest {

    @Test
    void testParseSearchInfoCVSS() {
        SearchService searchService = new SearchService();
        String infoType = "cvssScores";
        String infoArrStr = "CRITICAL;HIGH;LOW;MEDIUM";
        HashMap<String, String[]> ret = searchService.parseSearchInfo(infoType, infoArrStr);
        assertTrue(ret.containsKey(infoType));
        assertArrayEquals(new String[]{"CRITICAL", "HIGH", "LOW", "MEDIUM"}, ret.get(infoType));
    }

    @Test
    void testParseSearchInfoVDO() {
        SearchService searchService = new SearchService();
        String infoType = "vdoNounGroups";
        String infoArrStr = "Impact Method:Man-in-the-Middle;Authentication Bypass;Code Execution;Context Escape;Trust Failure|Context:Channel;Physical Hardware;Application;Host OS;Firmware;Guest OS;Hypervisor|Mitigation:Physical Security;MultiFactor Authentication;HPKP/HSTS;ASLR;Sandboxed|Attack Theater:Limited Rmt;Local;Remote;Physical|Logical Impact:Read;Resource Removal;Write;Indirect Disclosure;Service Interrupt;Privilege Escalation";
        HashMap<String, String[]> ret = searchService.parseSearchInfo(infoType, infoArrStr);
        assertTrue(ret.containsKey("Impact Method"));
        assertArrayEquals(new String[]{"Man-in-the-Middle", "Authentication Bypass", "Code Execution", "Context Escape", "Trust Failure"}, ret.get("Impact Method"));
    }


}