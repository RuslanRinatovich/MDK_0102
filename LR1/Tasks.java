package LR1;

import java.util.Scanner;

public class Tasks {
    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите число x = ");
        double x = scanner.nextDouble();
        if (x >= -3)
            if (x <= 9)
                if (x > 1)
                    System.out.println("не принадлежит");
                else
                    System.out.println("принадлежит");

    }

}

