package com.example.zxl.criminalintent;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ZXL on 2015/9/24.
 */
public class FormatDateTime {

    public static String toLongDateString(Date dt){
        SimpleDateFormat myFmt=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 E ");
        return myFmt.format(dt);
    }


}
