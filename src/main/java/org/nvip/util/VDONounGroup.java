package org.nvip.util;

public enum VDONounGroup{
    IMPACT_METHOD(1,  "Impact Method"),
    CONTEXT(2,  "Context"),
    MITIGATION(3, "Mitigation"),
    ATTACK_THEATER(4,  "Attack Theater"),
    LOGICAL_IMPACT(5, "Logical Impact");

    public int vdoNounGroupId;
    public String vdoNounGroupName;

    VDONounGroup(int vdoNounGroupId, String vdoNounGroupName) {
        this.vdoNounGroupId = vdoNounGroupId;
        this.vdoNounGroupName = vdoNounGroupName;
    }
    public int getVdoGroupId() {
        return vdoNounGroupId;
    }
    public static VDONounGroup getVdoNounGroup(int vdoNounGroupId){
        for(VDONounGroup vdo : VDONounGroup.values()){
            if (vdoNounGroupId == vdo.getVdoGroupId()){
                return vdo;
            }
        }
        return null;
    }

    public static VDONounGroup getVdoNounGroup(String vdoNounGroupName){
        for(VDONounGroup vdo : VDONounGroup.values()){
            if (vdoNounGroupName.equals(vdo.vdoNounGroupName)){
                return vdo;
            }
        }
        return null;
    }

}