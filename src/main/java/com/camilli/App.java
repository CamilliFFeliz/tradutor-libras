package com.camilli;

import nu.pattern.OpenCV; // Esta linha traz o OpenCV
import org.opencv.core.Core; // Esta linha traz as funções do núcleo

public class App {
    public static void main(String[] args) {
        
        System.out.println("Tentando carregar o OpenCV...");

        // Esta linha carrega a biblioteca (DLLs) automaticamente
        OpenCV.loadLocally();

        System.out.println("-------------------------------------------");
        System.out.println("SUCESSO!");
        System.out.println("Versão do OpenCV carregada: " + Core.VERSION);
        System.out.println("-------------------------------------------");
    }
}