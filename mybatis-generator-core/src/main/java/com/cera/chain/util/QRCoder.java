package com.cera.chain.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class QRCoder {
	private static Logger logger = Logger.getLogger(QRCoder.class);
	
	public static BufferedImage generateQRCode(String content){
        int width = 300;   
        int height = 300;
        Hashtable<EncodeHintType,String> hints= new Hashtable<EncodeHintType,String>();   
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");   
        
        try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height,hints);
			return MatrixToImageWriter.toBufferedImage(bitMatrix);
		} catch (WriterException e) {
			logger.error("生成二维码失败", e);
		}
        
        return null;
	}
	
	public static void main(String[] args) {
		String content = "http://192.168.0.17:8080/mobile/index.html";
		BufferedImage img = QRCoder.generateQRCode(content);
		String format = "png";
		File outputFile = new File("D:/new.png");
		
		try {
			ImageIO.write(img, format, outputFile);
			logger.error("生成二维码成功！ "+outputFile.getPath());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

}
