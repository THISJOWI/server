package uk.thisjowi.OTP.service;

import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@Service
public class QrService {
    public String decodeQrFromBase64(String base64Image) throws Exception {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}
