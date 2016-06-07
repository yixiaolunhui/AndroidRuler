package com.joey.ruler.library;

public class RulerError {
    
//  类型不对
    public static final int ERROR_AUTHOR = 0;
//    数值越界
    public static final int ERROR_OVER = 1;
//    数值未负数
    public static final int ERROR_NEGATIVE = 2;
    
    String errorMsg;
    int code;
    
    public RulerError(int code){
        switch(code){
            case ERROR_AUTHOR:
                errorMsg = "参数不正确";
                break;
            case ERROR_OVER:
                errorMsg = "数值越界";
                break;
            case ERROR_NEGATIVE:
                errorMsg = "为负数";
                break;
        }
    }
        

}
