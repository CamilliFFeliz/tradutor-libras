package com.camilli;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

import nu.pattern.OpenCV;

public class App {

    public static void main(String[] arg){
        OpenCV.loadLocally();

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()){
            System.out.println("Error: Foi identificado um erro ao abrir a camera.");
            return;
        }

        Rect retanguloArea = new Rect (300,50,300,300);

        Mat frame = new Mat();
        Mat frameRecortado = new Mat();
        Mat imagemCinza = new Mat();
        Mat imagemDesfocada = new Mat();
        Mat imagemBinaria = new Mat();

        System.out.println("Sistema iniciado, ajuste sua iluminação para melhor compreensão");
            while (camera.read(frame)){

                Core.flip(frame, frame, 1);
                Imgproc.rectangle(frame, retanguloArea, new Scalar(0,255,0),2);
                frameRecortado = frame.submat(retanguloArea);
                Imgproc.cvtColor(frameRecortado, imagemCinza, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(imagemCinza, imagemDesfocada, new Size(5,5),0);
                Imgproc.threshold(imagemDesfocada, imagemBinaria, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);


                Mat visualizcao = new Mat();
                Imgproc.cvtColor(imagemBinaria, visualizcao, Imgproc.COLOR_GRAY2BGR);

                Mat redimensionado = new Mat();
                Imgproc.resize(visualizcao, redimensionado, new Size(150,150));

                Mat areaDeColagem = frame.submat(new Rect(10, 50, 150, 150));
                redimensionado.copyTo(areaDeColagem);

            Imgproc.putText(frame, "Area de Visao do Computador", new Point(10,40),
                Imgproc.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 0, 255), 1);

            HighGui.imshow("Tradutor de Libras - Versão 1.0", frame);

            if (HighGui.waitKey(30) == 27) {
                break;
            }
            
        }

        camera.release();
        HighGui.destroyAllWindows();
        System.exit(0);

    }
}