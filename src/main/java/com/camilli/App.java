package com.camilli;

import java.io.File;
import java.util.Scanner;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point; // <--- FALTAVA ESTA LINHA
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import nu.pattern.OpenCV;

public class App {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        
        // Criar pasta para salvar as imagens se não existir
        File pastaDataset = new File("dataset_libras");
        if (!pastaDataset.exists()) {
            pastaDataset.mkdir();
        }

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) return;

        Rect retanguloArea = new Rect(300, 50, 300, 300);
        Mat frame = new Mat();
        Mat imagemCinza = new Mat();
        Mat imagemDesfocada = new Mat();
        Mat imagemBinaria = new Mat();
        Mat imagemSalvar = new Mat(); 

        Scanner scanner = new Scanner(System.in);
        int contadorFotos = 0;

        System.out.println("--- MODO GRAVAÇÃO DE DATASET ---");
        System.out.println("1. Posicione a mão no quadrado.");
        System.out.println("2. Aperte 'S' no teclado para salvar.");
        System.out.println("3. Digite o nome da letra no terminal.");

        while (camera.read(frame)) {
            Core.flip(frame, frame, 1);
            
            Mat areaMao = frame.submat(retanguloArea);
            Imgproc.cvtColor(areaMao, imagemCinza, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(imagemCinza, imagemDesfocada, new Size(5, 5), 0);
            Imgproc.threshold(imagemDesfocada, imagemBinaria, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            Imgproc.resize(imagemBinaria, imagemSalvar, new Size(50, 50));

            Imgproc.rectangle(frame, retanguloArea, new Scalar(0, 255, 0), 2);
            // Agora esta linha vai funcionar:
            Imgproc.putText(frame, "Aperte 'S' para salvar", new Point(10, 50), 1, 1.5, new Scalar(0, 255, 255), 2);

            Mat visualizacao = new Mat();
            Imgproc.cvtColor(imagemBinaria, visualizacao, Imgproc.COLOR_GRAY2BGR);
            Imgproc.resize(visualizacao, visualizacao, new Size(150, 150));
            visualizacao.copyTo(frame.submat(new Rect(10, 100, 150, 150)));

            HighGui.imshow("Gravador de LIBRAS", frame);

            int tecla = HighGui.waitKey(30);
            
            if (tecla == 27) break; // ESC para sair

            if (tecla == 's' || tecla == 'S') {
                System.out.print("Qual letra é essa? (Digite e dê Enter): ");
                String nomeLetra = scanner.next();
                
                // Salva com nome único usando o tempo do sistema
                String nomeArquivo = "dataset_libras/" + nomeLetra + "_" + System.currentTimeMillis() + ".png";
                Imgcodecs.imwrite(nomeArquivo, imagemSalvar);
                
                System.out.println("Salvo: " + nomeArquivo);
                contadorFotos++;
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
        scanner.close();
        System.exit(0);
    }
}