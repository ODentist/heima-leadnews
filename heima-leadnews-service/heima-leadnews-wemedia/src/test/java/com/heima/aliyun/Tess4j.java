package com.heima.aliyun;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

/**
 * @Description TODO
 * @Author bo.li
 * @Date 2023/4/23 9:41
 * @Version 1.0
 */
public class Tess4j {

    public static void main(String[] args) throws TesseractException {

        ITesseract tesseract = new Tesseract();
        //设置字体库路径
        tesseract.setDatapath("D:\\heimanews\\tessdata");
        //设置语音
        tesseract.setLanguage("chi_sim");

        String result = tesseract.doOCR(new File("D:\\heimanews\\2.png"));
        System.out.println(result);
    }

}