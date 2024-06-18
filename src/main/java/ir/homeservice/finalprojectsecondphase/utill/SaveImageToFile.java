package ir.homeservice.finalprojectsecondphase.utill;

import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
@Component
public class SaveImageToFile {
      public static void saveImageToFile(byte[] imageBytes, String filePath) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                  fileOutputStream.write(imageBytes);
            } catch (IOException e) {
                  e.printStackTrace();
            }
      }
}