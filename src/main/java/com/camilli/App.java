package com.camilli;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import nu.pattern.OpenCV;

public class App {
    public static void main(String[] args) {
        OpenCV.loadLocally();
        
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) return;

        Rect retanguloArea = new Rect(300, 50, 300, 300);
        
        Mat frame = new Mat();
        Mat imagemCinza = new Mat();
        Mat imagemDesfocada = new Mat();
        Mat imagemBinaria = new Mat();

        List<MatOfPoint> contornos = new ArrayList<>();
        Mat hierarquia = new Mat();

        while (camera.read(frame)) {
            Core.flip(frame, frame, 1);
            
            Mat areaMao = frame.submat(retanguloArea);
            Imgproc.cvtColor(areaMao, imagemCinza, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(imagemCinza, imagemDesfocada, new Size(5, 5), 0);
            Imgproc.threshold(imagemDesfocada, imagemBinaria, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            contornos.clear();
            Imgproc.findContours(imagemBinaria, contornos, hierarquia, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            int indiceMaiorContorno = -1;
            double maiorArea = 0;

            for (int i = 0; i < contornos.size(); i++) {
                double area = Imgproc.contourArea(contornos.get(i));
                if (area > maiorArea) {
                    maiorArea = area;
                    indiceMaiorContorno = i;
                }
            }

            if (indiceMaiorContorno != -1) {
                MatOfPoint maoDetectada = contornos.get(indiceMaiorContorno);
                
                Imgproc.drawContours(frame, contornos, indiceMaiorContorno, new Scalar(255, 0, 0), 2, 8, hierarquia, 0, new Point(retanguloArea.x, retanguloArea.y));

                MatOfInt hullInt = new MatOfInt();
                Imgproc.convexHull(maoDetectada, hullInt);

                if (hullInt.toArray().length > 3) {
                    MatOfInt4 defeitos = new MatOfInt4();
                    Imgproc.convexityDefects(maoDetectada, hullInt, defeitos);

                    int contagemDedos = 0;
                    List<Integer> dadosDefeitos = defeitos.toList();

                    for (int i = 0; i < dadosDefeitos.size(); i += 4) {
                        Point end = maoDetectada.toList().get(dadosDefeitos.get(i + 1));
                        Point far = maoDetectada.toList().get(dadosDefeitos.get(i + 2));
                        float depth = dadosDefeitos.get(i + 3) / 256.0f;

                        if (depth > 20) { 
                            contagemDedos++;
                            Point pF = new Point(far.x + retanguloArea.x, far.y + retanguloArea.y);
                            Imgproc.circle(frame, pF, 4, new Scalar(0, 0, 255), -1);
                        }
                    }
                    
                    String mensagem = (contagemDedos == 0) ? "1 Dedo / Fechada" : (contagemDedos + 1) + " Dedos";
                    Imgproc.putText(frame, mensagem, new Point(50, 50), Imgproc.FONT_HERSHEY_SIMPLEX, 1.5, new Scalar(0, 255, 255), 2);
                }
            }

            Imgproc.rectangle(frame, retanguloArea, new Scalar(0, 255, 0), 1);
            
            Mat visualizacao = new Mat();
            Imgproc.cvtColor(imagemBinaria, visualizacao, Imgproc.COLOR_GRAY2BGR);
            Mat redimensionada = new Mat();
            Imgproc.resize(visualizacao, redimensionada, new Size(150, 150));
            redimensionada.copyTo(frame.submat(new Rect(10, 80, 150, 150)));

            HighGui.imshow("Tradutor LIBRAS", frame);

            if (HighGui.waitKey(30) == 27) break;
        }

        camera.release();
        HighGui.destroyAllWindows();
        System.exit(0);
    }
}