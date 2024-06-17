package com.fenbeitong.openapi.plugin.seeyon.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FbOrgEmpProcessFactory {
    @Autowired
    FbEmpCreateProcess fbEmpCreateProcess;
    @Autowired
    FbEmpUpdateProcess fbEmpUpdateProcess;
    @Autowired
    FbEmpDeleteProcess fbEmpDeleteProcess;
    @Autowired
    FbOrgCreateProcess fbOrgCreateProcess;
    @Autowired
    FbOrgUpdateProcess fbOrgUpdateProcess;
    @Autowired
    FbOrgDeleteProcess fbOrgDeleteProcess;

    public IFbOrgEmpProcess getFbOrgEmpProcessor(String  processType) {
        switch (processType) {
            case "a":
                return fbEmpDeleteProcess;
            case "b":
                return fbOrgDeleteProcess;
            case "c":
                return fbOrgCreateProcess;
            case "d":
                return fbEmpCreateProcess;
            case "e":
                return fbEmpUpdateProcess;
            case "f":
                return fbOrgUpdateProcess;
            default:
                return null;
        }
    }
}
