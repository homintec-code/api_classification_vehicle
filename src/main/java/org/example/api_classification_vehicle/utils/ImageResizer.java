package org.example.api_classification_vehicle.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ImageResizer {

    public static String resizeBase64Image(String originalBase64, int targetWidth, int targetHeight) {
        try {
            // Décoder l'image originale
            byte[] imageBytes = Base64.getDecoder().decode(originalBase64);
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            // Calculer les nouvelles dimensions en conservant le ratio
            double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
            int newWidth, newHeight;

            if (targetWidth / aspectRatio <= targetHeight) {
                newWidth = targetWidth;
                newHeight = (int) (targetWidth / aspectRatio);
            } else {
                newHeight = targetHeight;
                newWidth = (int) (targetHeight * aspectRatio);
            }

            // Création de l'image redimensionnée
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // Encodage en JPEG (pour compression)
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", outputStream);

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Échec du redimensionnement", e);
        }
    }
}