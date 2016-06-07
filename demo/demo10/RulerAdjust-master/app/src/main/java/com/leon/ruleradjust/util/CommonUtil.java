package com.leon.ruleradjust.util;

import java.math.BigDecimal;

public class CommonUtil{
    /*四舍五入保留整数*/
    public static int doubleToInt(double num){
        BigDecimal bd=new BigDecimal(num);
        bd=bd.setScale(0, BigDecimal.ROUND_HALF_UP);
        return bd.intValue();
    }
}
