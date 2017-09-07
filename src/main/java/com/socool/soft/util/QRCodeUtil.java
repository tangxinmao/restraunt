package com.socool.soft.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {
	
	public static String encode(String url, String filePath, String fileName, String suffix, int width, int height) throws WriterException, IOException {  
		File dir = new File(filePath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);

        String qrcodePath = filePath + fileName + "_original." + suffix;
        File qrcodeFile = new File(qrcodePath);
        MatrixToImageWriter.writeToStream(bitMatrix, suffix, new FileOutputStream(qrcodeFile));
        return qrcodePath;
    }
	
	public static void main(String[] args) throws WriterException, IOException {
		String fileInfoId = UUID.randomUUID().toString().replaceAll("-", "");
		String dirPath = "D:\\Tools\\apache-tomcat-9.0.0.M17\\static\\images\\temp\\";
		String qrcodePath = encode("baidu.com", dirPath, fileInfoId, "png", 280, 280);
		BufferedImage qrcode = ImageIO.read(new File(qrcodePath));
		System.out.println(QRCodeUtil.class.getResource("/").getPath());
		String backgroundImgPath = "D:\\Tools\\apache-tomcat-9.0.0.M17\\webapps\\restraunt\\static\\images\\KS_QR_code.png";
		BufferedImage background = ImageIO.read(new File(backgroundImgPath));
		Graphics g = background.getGraphics();
        g.drawImage(qrcode, 70, 74, 280, 280, null);
        g.setFont(new Font(null, Font.BOLD, 40));
        g.setColor(new Color(72, 43, 6));
        g.drawString("Table: 001", 110, 60);
        File realQrcode = new File(dirPath + fileInfoId + "." + "png");
        ImageIO.write(background, "png", realQrcode);
	}
}
