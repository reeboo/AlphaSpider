/*
 * Copyright 2014 Qunar.com All right reserved. This software is the
 * confidential and proprietary information of Qunar.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Qunar.com.
 */
package com.fun.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 实现描述：tesseract图像识别服务
 *
 * @author reeboo
 * @version v1.0.0
 * @see
 * @since 2014年9月4日 下午4:38:13
 */
public class TesseractUtil {

    /**
     * 从图片中识别整数
     *
     * @param imageFile
     * @return
     * @throws java.io.IOException
     */
    public static String recognizeInteger(File imageFile) throws IOException {
        return recognizeInteger(imageFile, 3, true);
    }

    public static String recognizeInteger(File imageFile, int enlargeTimes, boolean isEnlarge) throws IOException {
        // 放大3倍，提高识别率
        File tmpScaledImage = File.createTempFile("tesseract-ocr-scaled", null);
        tmpScaledImage.deleteOnExit();
        if (isEnlarge)
            scaled(imageFile, enlargeTimes, tmpScaledImage);

        // 输出文件
        File tmpOutputBase = new File(tmpScaledImage.getAbsolutePath() + ".out");
        File tmpOutputText = new File(tmpScaledImage.getAbsolutePath() + ".out.txt");

        try {
            // 执行tesseract
            int exitCode = Runtime.getRuntime().exec(new String[]{"tesseract", // command
                    tmpScaledImage.getAbsolutePath(), // imagename
                    tmpOutputBase.getAbsolutePath(), // outputbase
                    "-psm", "8", // pagesegmode, treat the image as a single word
            }).waitFor();
            tmpScaledImage.delete(); // 删除临时文件
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 从输出文件中读取结果
        String text = Files.readFirstLine(tmpOutputText, Charsets.UTF_8);
        tmpOutputText.delete(); // 删除临时文件
        if (StringUtils.isNotBlank(text)) {
            return text.trim().replaceAll("\\s|,", "");
        }
        tmpOutputBase.delete();

        return StringUtils.EMPTY;
    }

    // 放大图片
    private static void scaled(File imageFile, int times, File targetFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        int targetWidth = image.getWidth() * times;
        int targetHeight = image.getHeight() * times;
        int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
                : BufferedImage.TYPE_INT_ARGB;
        BufferedImage tmp = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D g2 = tmp.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();
        ImageIO.write(tmp, "png", targetFile);
    }

}
