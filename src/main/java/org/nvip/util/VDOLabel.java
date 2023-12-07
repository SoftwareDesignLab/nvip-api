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

package org.nvip.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum VDOLabel {

    TRUST_FAILURE(1, "Trust Failure", VDONounGroup.IMPACT_METHOD),
    MAN_IN_THE_MIDDLE(2, "Man-in-the-Middle", VDONounGroup.IMPACT_METHOD),
    CHANNEL(3, "Channel", VDONounGroup.CONTEXT),
    AUTHENTICATION_BYPASS(4, "Authentication Bypass", VDONounGroup.IMPACT_METHOD),
    PHYSICAL_HARDWARE(5, "Physical Hardware", VDONounGroup.CONTEXT),
    APPLICATION(6, "Application", VDONounGroup.CONTEXT),
    HOST_OS(7, "Host OS", VDONounGroup.CONTEXT),
    FIRMWARE(8, "Firmware", VDONounGroup.CONTEXT),
    CODE_EXECUTION(9, "Code Execution", VDONounGroup.IMPACT_METHOD),
    CONTEXT_ESCAPE(10, "Context Escape", VDONounGroup.IMPACT_METHOD),
    GUEST_OS(11, "Guest OS", VDONounGroup.CONTEXT),
    HYPERVISOR(12, "Hypervisor", VDONounGroup.CONTEXT),
    SANDBOXED(13, "Sandboxed", VDONounGroup.MITIGATION),
    PHYSICAL_SECURITY(14, "Physical Security", VDONounGroup.MITIGATION),
    ASLR(15, "ASLR", VDONounGroup.MITIGATION),
    LIMITED_RMT(16, "Limited Rmt", VDONounGroup.ATTACK_THEATER),
    LOCAL(17, "Local", VDONounGroup.ATTACK_THEATER),
    READ(18, "Read", VDONounGroup.LOGICAL_IMPACT),
    RESOURCE_REMOVAL(19, "Resource Removal", VDONounGroup.LOGICAL_IMPACT),
    HPKP_HSTS(20, "HPKP/HSTS", VDONounGroup.MITIGATION),
    MULTIFACTOR_AUTHENTICATION(21, "MultiFactor Authentication", VDONounGroup.MITIGATION),
    REMOTE(22, "Remote", VDONounGroup.ATTACK_THEATER),
    WRITE(23, "Write", VDONounGroup.LOGICAL_IMPACT),
    INDIRECT_DISCLOSURE(24, "Indirect Disclosure", VDONounGroup.LOGICAL_IMPACT),
    SERVICE_INTERRUPT(25, "Service Interrupt", VDONounGroup.LOGICAL_IMPACT),
    PRIVILEGE_ESCALATION(26, "Privilege Escalation", VDONounGroup.LOGICAL_IMPACT),
    PHYSICAL(27, "Physical", VDONounGroup.ATTACK_THEATER);

    public int vdoLabelId;
    public String vdoLabelName;
    public VDONounGroup vdoNounGroup;

    private static final Logger logger = LogManager.getLogger(VDOLabel.class);

    VDOLabel(int vdoLabelId, String vdoLabelName, VDONounGroup vdoNounGroup) {
        this.vdoLabelId = vdoLabelId;
        this.vdoLabelName = vdoLabelName;
        this.vdoNounGroup = vdoNounGroup;
    }
    public int getVdoLabelId() {
        return vdoLabelId;
    }
    public static VDOLabel getVdoLabel(String vdoLabelName){
        for (VDOLabel label : VDOLabel.values()){
            if (label.vdoLabelName.equals(vdoLabelName)){
                return label;
            }
        }
        return null;
    }
    public static VDOLabel getVdoLabel(int vdoLabelId){
        for(VDOLabel vdo : VDOLabel.values()){
            if (vdoLabelId == vdo.getVdoLabelId()){
                return vdo;
            }
        }
        return null;
    }
}
