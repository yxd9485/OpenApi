package com.fenbeitong.openapi.plugin.root;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Documentation;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;

/**
 * <p>Title: Swagger2Config</p>
 * <p>Description: </p>
 * <p>Company: www.fenbeitong.com</p>
 *
 * @author hwangsy
 * @date 2019/12/24 11:37 AM
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@ConditionalOnProperty(prefix = "swagger2", value = {"enable"}, havingValue = "true")
public class Swagger2Config {

    //    @Bean("funcApis")
    public Docket createFuncApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("func模块")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fenbeitong.openapi.plugin.func"))
            .paths(PathSelectors.regex("/func/.*"))
            .build()
            .apiInfo(apiInfo())
            .enable(true);
    }

    //    @Bean("dingtalkApis")
    public Docket createDingTalkApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("dingtalk模块")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fenbeitong.openapi.plugin.dingtalk"))
            .paths(PathSelectors.regex("/dingtalk/.*"))
            .build()
            .apiInfo(apiInfo())
            .enable(true);
    }

    //    @Bean("wechatApis")
    public Docket createWechatApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("wechat模块")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fenbeitong.openapi.plugin.wechat"))
            .paths(PathSelectors.regex("/wechat/.*"))
            .build()
            .apiInfo(apiInfo())
            .enable(true);
    }

    //    @Bean("definitionApis")
    public Docket createDefinitionApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("definition模块")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fenbeitong.openapi.plugin.definition"))
            .paths(PathSelectors.regex("/definitions/.*"))
            .build()
            .apiInfo(apiInfo())
            .enable(true);
    }

    //    @Bean("yiduijieApis")
    public Docket createYiDuiJieApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("yiduijie模块")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fenbeitong.openapi.plugin.yiduijie"))
            .paths(PathSelectors.regex("/yiduijie/.*"))
            .build()
            .apiInfo(apiInfo())
            .enable(true);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("openapi-plugin系统接口文档")
            .description("提供子模块func/子模块wechat/dingtalk的文档/definition的文档/yiduijie的文档")
            .termsOfServiceUrl("git@hack.fenbeijinfu.com:finhub/openapi-plugin.git")
            .version("1.0")
            .build();
    }

    @Bean("welinkApis")
    public Docket createWeLinkApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("welink模块")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.fenbeitong.openapi.plugin.welink"))
            .paths(PathSelectors.regex("/welink/.*"))
            .build()
            .apiInfo(apiInfo())
            .enable(true);
    }

    @Component
    @Primary
    @ConditionalOnProperty(prefix = "swagger2", value = {"enable"}, havingValue = "true")
    private static class ServiceModelToSwagger2MapperImplExt extends ServiceModelToSwagger2MapperImpl {

        @Override
        public Swagger mapDocumentation(Documentation from) {
//            Multimap<String, ApiListing> apiListings = from.getApiListings();
            Map<String, List<ApiListing>> apiListings = from.getApiListings();
            Swagger swagger = super.mapDocumentation(from);
            swagger.setPaths(mapApiListings(apiListings));
            return swagger;
        }

        protected Map<String, Path> mapApiListings(Multimap<String, ApiListing> apiListings) {
            Map<String, Path> paths = new LinkedHashMap<>();
            Multimap<String, ApiListing> apiListingMap = LinkedListMultimap.create();
            Iterator iter = apiListings.entries().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, ApiListing> entry = (Map.Entry<String, ApiListing>) iter.next();
                ApiListing apis = entry.getValue();
                List<ApiDescription> apiDesc = apis.getApis();
                List<ApiDescription> newApi = new ArrayList<>();
                for (ApiDescription a : apiDesc) {
                    newApi.add(a);
                }
                newApi.sort((left, right) -> {
                    int leftPos = left.getOperations().get(0).getPosition();
                    int rightPos = right.getOperations().get(0).getPosition();
                    int position = Integer.compare(leftPos, rightPos);
                    if (position == 0) {
                        position = left.getPath().compareTo(right.getPath());
                    }
                    return position;
                });
                try {
                    //因ApiListing的属性都是final故需要通过反射来修改值
                    ModifyFinalUtils.modify(apis, "apis", newApi);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                apiListingMap.put(entry.getKey(), apis);
            }
            for (ApiListing each : apiListingMap.values()) {
                for (ApiDescription api : each.getApis()) {
                    paths.put(api.getPath(), mapOperations(api, Optional.ofNullable(paths.get(api.getPath()))));
                }
            }
            return paths;
        }

        private Path mapOperations(ApiDescription api, Optional<Path> existingPath) {
            Path path = existingPath.orElse(new Path());
            for (springfox.documentation.service.Operation each : nullToEmptyList(api.getOperations())) {
                Operation operation = mapOperation(each, null);
                path.set(each.getMethod().toString().toLowerCase(), operation);
            }
            return path;
        }
    }

    private static class ModifyFinalUtils {

        public static void modify(Object object, String fieldName, Object newFieldValue) throws Exception {
            Field field = object.getClass().getDeclaredField(fieldName);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            //Field 的 modifiers 是私有的
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(object, newFieldValue);
        }
    }

}
