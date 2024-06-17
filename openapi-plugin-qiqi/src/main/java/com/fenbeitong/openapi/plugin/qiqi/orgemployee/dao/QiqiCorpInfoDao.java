package com.fenbeitong.openapi.plugin.qiqi.orgemployee.dao;

import com.fenbeitong.openapi.plugin.core.db.OpenApiBaseDao;
import com.fenbeitong.openapi.plugin.qiqi.entity.QiqiCorpInfo;
import com.fenbeitong.openapi.plugin.support.common.exception.OpenApiPluginSupportException;
import com.fenbeitong.openapi.plugin.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

import static com.fenbeitong.openapi.plugin.support.common.constant.SupportRespCode.LACK_NECESSARY_PARAM;

/**
 * @ClassName QiqiCorpInfoDao
 * @Description 企企对接数据查询
 * @Author helu
 * @Date 2022/5/13 下午10:27
 * @Company www.fenbeitong.com
 **/
@Slf4j
@Component
public class QiqiCorpInfoDao extends OpenApiBaseDao<QiqiCorpInfo> {

    /**
     * 根据appId查询企业授权配置
     */
    public QiqiCorpInfo getCorpIdByCompanyId(String companyId) {
        if (StringUtils.isTrimBlank(companyId)) {
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "companyId");}
        Example example = new Example(QiqiCorpInfo.class);
        example.createCriteria().andEqualTo("companyId", companyId);
        return getByExample(example);
    }

    /**
     * 根据三方企业id查询企业授权配置
     */
    public QiqiCorpInfo getBycorpId(String corpId) {
        if (StringUtils.isTrimBlank(corpId)) {
            throw new OpenApiPluginSupportException(LACK_NECESSARY_PARAM, "corpId");}
        Example example = new Example(QiqiCorpInfo.class);
        example.createCriteria().andEqualTo("openId", corpId);
        return getByExample(example);
    }

    /**
     * 查询所有企业授权配置
     */
    public List<QiqiCorpInfo> getQiqiCorp() {
        Example example = new Example(QiqiCorpInfo.class);
        example.createCriteria().andCondition("1=1");
        return listByExample(example);
    }

}
