package com.fenbeitong.openapi.plugin.demo;

import com.fenbeitong.openapi.plugin.demo.dto.ApprovalInfoXmlEntity;
import com.fenbeitong.openapi.plugin.demo.dto.ApproveXmlEntity;
import com.fenbeitong.openapi.plugin.demo.dto.DetailsXmlEntity;
import com.fenbeitong.openapi.plugin.demo.dto.SpRecordXmlEntity;
import com.fenbeitong.openapi.plugin.util.JsonUtils;
import com.fenbeitong.openapi.plugin.util.xml.XmlUtil;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * <p>Title: XmlTest</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/12 4:05 PM
 */
public class XmlTest {

    @Test
    public void testXml() {
//        DetailsXmlEntity details1 = new DetailsXmlEntity();
//        details1.setSpeech("1");
//        details1.setSpStatus(0);
//        details1.setSpTime(0L);
//        DetailsXmlEntity details2 = new DetailsXmlEntity();
//        details2.setSpeech("2");
//        details2.setSpStatus(0);
//        details2.setSpTime(0L);
//        SpRecordXmlEntity spRecordXmlEntity = new SpRecordXmlEntity();
//        spRecordXmlEntity.setSpNo("201212121212");
//        spRecordXmlEntity.setSpStatus(0);
//        spRecordXmlEntity.setDetailsList(Lists.newArrayList(details1, details2));
//        ApprovalInfoXmlEntity approveInfoXmlEntity = new ApprovalInfoXmlEntity();
//        approveInfoXmlEntity.setSpNo("x112121212");
//        approveInfoXmlEntity.setSpName("sssssssss");
//        approveInfoXmlEntity.setSpRecord(spRecordXmlEntity);
//        ApproveXmlEntity xmlEntity = new ApproveXmlEntity();
//        xmlEntity.setToUserName("张三");
//        xmlEntity.setFromUserName("李四");
//        xmlEntity.setCreateTime(100012131L);
//        xmlEntity.setApprovalInfo(approveInfoXmlEntity);
//        String xml = XmlUtil.object2Xml(xmlEntity);
        String xml = "<xml>\n" +
                "  <ToUserName><![CDATA[张三]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[李四]]></FromUserName>\n" +
                "  <CreateTime>100012131</CreateTime>\n" +
                "  <ApprovalInfo>\n" +
                "    <SpNo><![CDATA[x112121212]]></SpNo>\n" +
                "    <SpName><![CDATA[sssssssss]]></SpName>\n" +
                "    <SpRecord>\n" +
                "      <SpNo>201212121212</SpNo>\n" +
                "      <SpStatus>0</SpStatus>\n" +
                "      <Details>\n" +
                "        <Speech><![CDATA[1]]></Speech>\n" +
                "        <SpStatus>0</SpStatus>\n" +
                "        <SpTime>0</SpTime>\n" +
                "      </Details>\n" +
                "      <Details>\n" +
                "        <Speech><![CDATA[2]]></Speech>\n" +
                "        <SpStatus>0</SpStatus>\n" +
                "        <SpTime>0</SpTime>\n" +
                "      </Details>\n" +
                "    </SpRecord>\n" +
                "  </ApprovalInfo>\n" +
                "</xml>";
        System.out.println(xml);
        ApproveXmlEntity entity = (ApproveXmlEntity) XmlUtil.xml2Object(xml, ApproveXmlEntity.class);
        System.out.println(JsonUtils.toJson(entity));
    }

}
