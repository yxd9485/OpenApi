package com.fenbeitong.openapi.plugin.seeyon.service;

import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountEmpResp;
import com.fenbeitong.openapi.plugin.seeyon.dto.SeeyonAccountOrgResp;
import com.fenbeitong.openapi.plugin.seeyon.entity.*;

import java.util.List;

public interface SeeyonFbOrgEmpService {

    /**
     * @author Created by ivan on 2:43 PM 4/13/19.
     *     <p>createOrg
     * @param seeyonClient :
     * @param accountOrgResponse :
     * @return boolean
     */
    boolean createOrg(SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse);

    /**
     * @author Created by ivan on 2:43 PM 4/13/19.
     *     <p>updateOrg
     * @param seeyonClient :
     * @param accountOrgResponse :
     * @return boolean
     */
    boolean updateOrg(SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse);

    /**
     * @author Created by ivan on 2:43 PM 4/13/19.
     *     <p>delOrg
     * @param seeyonClient :
     * @param accountOrgResponse :
     * @return boolean
     */
    boolean delOrg(SeeyonClient seeyonClient, SeeyonAccountOrgResp accountOrgResponse);

    /**
     * @author Created by ivan on 2:43 PM 4/13/19.
     *     <p>delOrg
     * @param seeyonClient : seeyon client
     * @param seeyonOrgDepartment : department param
     * @return boolean :
     */
    boolean delOrg(SeeyonClient seeyonClient, SeeyonOrgDepartment seeyonOrgDepartment);

    /**
     * @author Created by ivan on 2:52 PM 4/13/19.
     *     <p>createEmp
     * @param seeyonClient :
     * @param accountEmpResponse :
     * @return boolean
     */
    boolean createEmp(SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse, SeeyonExtInfo seeyonExtInfo);

    /**
     * @author Created by ivan on 2:52 PM 4/13/19.
     *     <p>updateEmp
     * @param seeyonClient :
     * @param accountEmpResponse :
     * @return boolean
     */
    boolean updateEmp(SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse,SeeyonExtInfo seeyonExtInfo);

    /**
     * @author Created by ivan on 2:52 PM 4/13/19.
     *     <p>delEmp
     * @param seeyonClient :
     * @param accountEmpResponse :
     * @return boolean
     */
    boolean delEmp(SeeyonClient seeyonClient, SeeyonAccountEmpResp accountEmpResponse);

    /**
     * @author Created by ivan on 2:52 PM 4/13/19.
     *     <p>delEmp
     * @param seeyonClient :
     * @param seeyonOrgEmployee :
     * @return boolean:
     */
    boolean delEmp(SeeyonClient seeyonClient, SeeyonOrgEmployee seeyonOrgEmployee);

    boolean saveEmp(SeeyonOrgEmployee seeyonOrgEmployee);

    List<SeeyonFbOrgEmp> getSeeyonFbOrgEmps(SeeyonClient seeyonClient);
    List<SeeyonFbOrgEmp> getSeeyonFbOrgEmpsDesc(SeeyonClient seeyonClient);
    List<SeeyonFbOrgEmp> getSeeyonFbOrgEmpsByfbOrgEmp(SeeyonFbOrgEmp seeyonFbOrgEmp);
    List<SeeyonFbOrgEmp> filterList(SeeyonClient seeyonClient,List<SeeyonFbOrgEmp> sourceList);

}
