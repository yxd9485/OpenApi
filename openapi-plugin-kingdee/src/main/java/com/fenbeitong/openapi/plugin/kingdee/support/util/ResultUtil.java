package com.fenbeitong.openapi.plugin.kingdee.support.util;

import com.fenbeitong.openapi.plugin.kingdee.support.dto.ResultVo;
import com.fenbeitong.openapi.plugin.support.callback.constant.ResultEnum;


public class ResultUtil {
    /**
     * 成功结果集，不带返回数据
     * @return
     */
    public static ResultVo success(){
        ResultVo resultVo = new ResultVo(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),null);
        return resultVo;
    }

    /**
     * 成功结果集，自定义消息类型
     * @return
     */
    public static ResultVo success(String msg){
        ResultVo resultVo = new ResultVo(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),msg);
        return resultVo;
    }


    /**
     * 成功结果集，带返回数据
     * @return
     */
    public static ResultVo success(Object data){
        ResultVo resultVo = buildResultVo(data);
        if(resultVo!=null){
            return resultVo;
        }else{
            return new ResultVo(ResultEnum.SUCCESS.getCode(),ResultEnum.SUCCESS.getMsg(),data);
        }
    }

    /**
     * 成功结果集，带返回数据,自定义消息类型
     * @return
     */
    public static ResultVo success(String msg,Object data){
        ResultVo resultVo = new ResultVo(ResultEnum.SUCCESS.getCode(),msg,data);
        return resultVo;
    }

    /**
     * 失败结果集，不带返回数据
     * @return
     */
    public static ResultVo error(){
        ResultVo resultVo = new ResultVo(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMsg(),null);
        return resultVo;
    }

    /**
     * 失败结果集，自定义消息类型
     * @return
     */
    public static ResultVo error(String msg){
        ResultVo resultVo = new ResultVo(1,msg,null);
        return resultVo;
    }

    /**
     * 失败结果集，自定义错误码和消息类型
     * @return
     */
    public static ResultVo error(Integer code ,String msg){
        ResultVo resultVo = new ResultVo(code,msg,null);
        return resultVo;
    }

    /**
     * 失败结果集，带返回数据
     * @return
     */
    public static ResultVo error(Object data){
        ResultVo resultVo = buildResultVo(data);
        if(resultVo!=null){
            return resultVo;
        }else{
            return new ResultVo(1,"error",data);
        }


    }

    /**
     * 失败结果集，带返回数据,自定义消息类型
     * @return
     */
    public static ResultVo error(String msg,Object data){
        ResultVo resultVo = new ResultVo(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMsg(),data);
        return resultVo;
    }

    /**
     * 根据枚举类型返回结果，不包含数据
     * @param resultEnum
     * @return
     */
    public static<T> ResultVo result(ResultEnum resultEnum){
        ResultVo resultVo = new ResultVo(resultEnum.getCode(),resultEnum.getMsg(),null);
        return resultVo;
    }

    /**
     * 根据枚举类型返回结果，包含数据
     * @param resultEnum
     * @return
     */
    public static ResultVo result(ResultEnum resultEnum,Object data){
        ResultVo resultVo = new ResultVo(resultEnum.getCode(),resultEnum.getMsg(),data);
        return resultVo;
    }


    public static ResultVo buildResultVo(Object data){
//        if(data instanceof ProductResultEnum){
//            ProductResultEnum productResultEnum = (ProductResultEnum) data;
//            return new ResultVo(productResultEnum.getCode(),productResultEnum.getMsg(),null);
//        }
//        if(data instanceof OrderResultEnum){
//            OrderResultEnum orderResultEnum = (OrderResultEnum) data;
//            return new ResultVo(orderResultEnum.getCode(),orderResultEnum.getMeg(),null);
//        }
//        if(data instanceof BusinessResultEnum){
//            BusinessResultEnum busiResultEnum = (BusinessResultEnum) data;
//            return new ResultVo(busiResultEnum.getCode(),busiResultEnum.getMeg(),null);
//        }
//
//        if(data instanceof NoticeEnum){
//            NoticeEnum noticeEnum = (NoticeEnum) data;
//            return new ResultVo(noticeEnum.getCode(),noticeEnum.getMsg(),null);
//        }
        return null;
    }

//    public static PageGrid emptyGrid(){
//        return new PageGrid(new ArrayList(),0);
//    }

    /**
     * 判空校验,为空抛出运行时异常
     * @param resultVo 判断对象
     * @param message 异常消息
     * @param <T> 类型
     * @return 不为空,返回原结果
     */
    public static <T> ResultVo<T> requireNonNull(ResultVo<T> resultVo, String message){
        if (resultVo == null || resultVo.getCode() != ResultEnum.SUCCESS.getCode() || resultVo.getData() == null) {
            throw new RuntimeException(message);
        }
        return resultVo;
    }
//    /**
//     * 判空校验,为空抛出运行时异常
//     * @param pageGrid 判断对象
//     * @param message 异常消息
//     * @param <T> 类型
//     * @return 不为空,返回原结果
//     */
//    public static <T> PageGrid<T> requireNonNull(PageGrid<T> pageGrid,String message){
//        if (Utils.gridIsEmpty(pageGrid)) {
//            throw new RuntimeException(message);
//        }
//        return pageGrid;
//    }


}
